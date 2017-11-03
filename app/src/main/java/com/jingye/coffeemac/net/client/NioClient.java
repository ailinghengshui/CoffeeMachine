package com.jingye.coffeemac.net.client;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;
import static org.jboss.netty.channel.Channels.write;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.jingye.coffeemac.net.netty.NioResponse;
import com.jingye.coffeemac.net.netty.PacketDecoder;
import com.jingye.coffeemac.net.netty.PacketEncoder;
import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ILinkService;
import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.pack.PackagePacker;
import com.jingye.coffeemac.service.protocol.request.HandshakeRequest;
import com.jingye.coffeemac.service.protocol.request.KeepAliveRequest;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.service.protocol.response.HandshakeResponse;
import com.jingye.coffeemac.util.log.LogUtil;


public class NioClient {

    /**
     * IO threads
     * one upstream and one downstream
     */
    private static final int IO_THREADS = 2;

    /**
     * state
     */
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_READY = 3;

    /**
     * callback interface
     */
    public interface Callback {
        /**
         * on state
         *
         * @param state
         */
        public void onState(int state);

        /**
         * on packet
         *
         * @param response
         */
        public void onPacket(NioResponse response);
    }

    /**
     * callback
     */
    private Callback mCallback;

    /**
     * state
     */
    private AtomicInteger mState = new AtomicInteger(STATE_DISCONNECTED);

    /**
     * channel
     */
    private Channel mChannel;

    /**
     * connect future, valid during connecting
     */
    private ChannelFuture mConnectFuture;

    private String uid;

    private PackagePacker packagePacker;
    private ChannelFactory clientFactory;
    private ClientBootstrap bootstrap;
    private PacketDecoder packetDecoder;
    private PacketEncoder packetEncoder;

    /**
     * idle handler
     */
    HashedWheelTimer mIdleTimer = new HashedWheelTimer();
    IdleStateHandler mIdleHandler = new IdleStateHandler(mIdleTimer, 4 * 60, 2 * 60, 0);

    /**
     * channel handler
     */
    private SimpleChannelHandler mChannelHandler = new IdleStateAwareChannelHandler() {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            // trace
            e.getCause().printStackTrace();
            e.getChannel().close();
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            // on disconnect
            LogUtil.vendor("wo ca, channelDisconnected");
            onDisconnect();
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            // handle NioResponse
            if (e.getMessage() instanceof NioResponse) {
                NioResponse packet = (NioResponse) e.getMessage();

                // handle response
                responseReceived(packet);
            }
        }

        @Override
        public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            // handle Pack
            if (e.getMessage() instanceof Pack) {
                // Pack to buffer
                Pack req = (Pack) e.getMessage();
                ChannelBuffer buffer = dynamicBuffer();
                buffer.writeBytes(req.getBuffer());

                // write buffer
                write(ctx, e.getFuture(), buffer);
            }
        }

        @Override
        public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
            if (e.getState() == IdleState.READER_IDLE) {
                // read idle, disconnect and login again
                LogUtil.vendor("i'm idle for more than 4 minites, server has been dead problably");
                onDisconnect();

            } else if (e.getState() == IdleState.WRITER_IDLE) {
                // write idle, send keep alive packet
                LogUtil.vendor("write idle for more than 2 minutes");
                if (isConnected()) {
                    LogUtil.vendor("write idle for more than 2 minutes with connect ready");
                    sendPacket(new KeepAliveRequest(uid));
                }
            }
        }
    };


    public NioClient(Callback callback) {
        mCallback = callback;

        setupNetty();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void connect() {
        if (mState.compareAndSet(STATE_DISCONNECTED, STATE_CONNECTING)) {
            doConnect();
        }
    }

    public void disconnect() {
        if (mState.compareAndSet(STATE_CONNECTING, STATE_DISCONNECTED)) {
            doDisconnect(STATE_CONNECTING);
        } else if (mState.compareAndSet(STATE_CONNECTED, STATE_DISCONNECTED)) {
            doDisconnect(STATE_CONNECTED);
        } else if (mState.compareAndSet(STATE_READY, STATE_DISCONNECTED)) {
            doDisconnect(STATE_READY);
        }
    }

    public boolean isConnected() {
        return mState.compareAndSet(STATE_READY, STATE_READY);
    }

    public boolean sendPacket(Request request) {
        if (!isConnected()) {
            return false;
        }

        sendRequested(request);

        return true;
    }

    /**
     * do connect
     */
    private void doConnect() {
        // prepare
        packagePacker = new PackagePacker();
        packetDecoder.setUnpacker(packagePacker);
        packetEncoder.setPacker(packagePacker);

        // link
        ServerAddressFetcher.IPAddress address = ServerAddressFetcher.sharedInstance().getLinkAddress();

        ChannelFuture connectFuture = null;
        // connect
        try {
            connectFuture = bootstrap.connect(new InetSocketAddress(address.ip, address.port));
        } catch (Exception e) {
            e.printStackTrace();
            onDisconnect();
            ServerAddressFetcher.sharedInstance().resetLinkAddress();
            return;
        }


        synchronized (this) {
            // save connect future for disconnect
            mConnectFuture = connectFuture;
        }

        // listen on connect future
        connectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                onConnectComplete(future);
            }
        });
    }

    /**
     * do disconnect
     *
     * @param state previous state
     */
    private void doDisconnect(int state) {
        /**
         * 1. STATE_READY | STATE_CONNECTED: close channel
         * 2. STATE_CONNECTING: cancel connect
         */

        Channel channel = null;
        ChannelFuture connectFuture = null;

        switch (state) {
            case STATE_READY:
            case STATE_CONNECTED:
                synchronized (this) {
                    // to close
                    channel = mChannel;

                    // recycle
                    mChannel = null;
                }

                break;
            case STATE_CONNECTING:
                synchronized (this) {
                    // to cancel
                    connectFuture = mConnectFuture;

                    // recycle
                    mConnectFuture = null;
                }

                break;
        }

        // cancel
        if (connectFuture != null) {
            connectFuture.cancel();
        }

        // close
        if (channel != null) {
            channel.close();
        }

        // reset all pipeline handler
        resetPipeline();
    }

    /**
     * on connect complete
     *
     * @param future
     */
    private void onConnectComplete(ChannelFuture future) {
        /**
         * connect complete, reset connect future
         */

        synchronized (this) {
            // recycle
            mConnectFuture = null;
        }

        /**
         * new state
         *
         * if success STATE_CONNECTED, otherwise STATE_DISCONNECTED
         */
        int state = future.isSuccess() ? STATE_CONNECTED : STATE_DISCONNECTED;

        /**
         * update state
         */
        if (mState.compareAndSet(STATE_CONNECTING, state)) {
            if (state == STATE_CONNECTED) {
                synchronized (this) {
                    mChannel = future.getChannel();
                }

                // on connected
                onConnected();
            } else {
                onDisconnect();
                ServerAddressFetcher.sharedInstance().resetLinkAddress();
            }
        } else {
            /**
             * here, maybe has been cancelled before connected
             */

            if (state == STATE_CONNECTED) {
                // close channel
                future.getChannel().close();
            }
        }
    }

    /**
     * on disconnect
     */
    private void onDisconnect() {
        if (mState.compareAndSet(STATE_CONNECTED, STATE_DISCONNECTED)) {
            doDisconnect(STATE_CONNECTED);
        } else if (mState.compareAndSet(STATE_READY, STATE_DISCONNECTED)) {
            doDisconnect(STATE_READY);
        } else if (mState.compareAndSet(STATE_CONNECTING, STATE_DISCONNECTED)) {
            doDisconnect(STATE_CONNECTING);
        }

        // on STATE_DISCONNECTED
        if (mCallback != null) {
            mCallback.onState(STATE_DISCONNECTED);
        }
    }

    /**
     * on connected
     */
    private void onConnected() {
        // start exchange key
        getSessionKey();
    }

    /**
     * on ready
     *
     * @param response
     */
    private void onReady(NioResponse response) {
        if (!mState.compareAndSet(STATE_CONNECTED, STATE_READY)) {
            return;
        }

        // handle session key
        setSessionKey(response);

        // on STATE_READY
        if (mCallback != null) {
            mCallback.onState(STATE_READY);
        }
    }

    private void sendRequested(Request request) {
        synchronized (this) {
            if (mChannel != null) {
                mChannel.write(request);
            }
        }
    }

    private void responseReceived(NioResponse response) {
        if (response.header.serviceId == ServiceID.SVID_LITE_LINK && response.header.commandId == ILinkService.CommandId.CID_EXCHANGE_KEY) {
            // on ready
            onReady(response);
        } else {
            // notify packet
            if (mCallback != null) {
                mCallback.onPacket(response);
            }
        }
    }

    private void getSessionKey() {
        sendRequested(new HandshakeRequest(uid, packagePacker.getModulus(), 0));
    }

    private void setSessionKey(NioResponse response) {
        HandshakeResponse handshakeResponse = new HandshakeResponse();
        handshakeResponse.setLinkFrame(response.header);
        try {
            if (!handshakeResponse.isSuccess()) {
                LogUtil.vendor("Handshake fail[code="
                        + handshakeResponse.getLinkFrame().resCode + "]");
            } else {
                handshakeResponse.unpackBody(response.body);
                LogUtil.vendor("Handshake succeed and rc4 key inited");
                byte[] key = packagePacker.decryptRc4Key(handshakeResponse
                        .getCodedRc4Key());
                packagePacker.setRc4Key(key);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setupNetty() {
        clientFactory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool(),
                IO_THREADS);

        bootstrap = new ClientBootstrap(clientFactory);
        packetEncoder = new PacketEncoder();
        packetDecoder = new PacketDecoder();
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("receiveBufferSize", 64 * 1024);
        bootstrap.setOption("bufferFactory",
                HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addFirst("idleHandler", mIdleHandler);
                pipeline.addLast("decoder", packetDecoder);
                pipeline.addLast("handler", mChannelHandler);
                pipeline.addLast("encoder", packetEncoder);
                return pipeline;
            }
        });
    }

    private void resetPipeline() {
        packetEncoder.reset();
        packetDecoder.reset();
    }
}
