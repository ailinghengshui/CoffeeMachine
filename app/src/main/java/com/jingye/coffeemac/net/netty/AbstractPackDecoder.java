package com.jingye.coffeemac.net.netty;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.jingye.coffeemac.service.protocol.PacketHeader;
import com.jingye.coffeemac.service.protocol.pack.UnpackException;
import com.jingye.coffeemac.util.log.LogUtil;


/**
 * @author weiliang(William)
 *         这个类是基本的解码器的基类，功能是对字节流进行包级别的分割，即，以包长度（前4字节）分割出包括包长度在内的一组字节
 *         此类的子类需要继承并实现parsePacket方法，以对分割出来的字节数组进行包的具体解码和处理，
 */
public abstract class AbstractPackDecoder extends FrameDecoder implements
		IPackParser {
	
	int packetSize = -1;
	byte[] sizeBytes = new byte[4];

	public AbstractPackDecoder() {
		super(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.handler.codec.frame.FrameDecoder#decode(org.jboss.netty
	 * .channel.ChannelHandlerContext, org.jboss.netty.channel.Channel,
	 * org.jboss.netty.buffer.ChannelBuffer)
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		ArrayList<Object> ret = new ArrayList<Object>();
		// 如果不满4字节长度字段，退出循环让通道将继续等待数据
		while (buffer.readableBytes() > 0) {
			if (packetSize == -1) { //先取出包的大小，存下来
				buffer.readBytes(sizeBytes);
				sizeBytes = decrypt(sizeBytes);
				packetSize = PacketHeader.bytesToLength(sizeBytes);
				if (packetSize <= 0)
					throw new UnpackException("Invalid packet, size=" + packetSize);
				packetSize += PacketHeader.getByteCount(packetSize);
			}

			LogUtil.i("core", "received packetSize: " + packetSize
					+ " readleBytes: " + buffer.readableBytes());
			// 缓冲区剩余字节数不足长度字段声明值，退出循环让通道继续等待数据
			if (buffer.readableBytes() < packetSize - 4)
				break;

			// 至此，通道缓冲区中已经包含完整的数据包
			// 将通道缓冲区中的数据包复制出来
			byte[] data = new byte[packetSize - 4];
			buffer.readBytes(data, 0, packetSize - 4);
			data = decrypt(data);
			byte[] packet = new byte[packetSize];
			System.arraycopy(sizeBytes, 0, packet, 0, 4);
			System.arraycopy(data, 0, packet, 4, packetSize-4);
			packetSize = -1;
			try {
				Object o = parsePacket(ctx, packet);
				if (o != null) {
					ret.add(o);
				} else {
					continue;
				}
			} catch (UnpackException ex) {
				LogUtil.vendor("Nio handle packet error");
			}
		}
		if (ret.size() > 0) {
			return ret;
		}
		return null;
	}

	private int convertirOctetEnEntier(byte[] b){    
	    int MASK = 0xFF;
	    int result = 0;   
	        result = b[0] & MASK;
	        result = result + ((b[1] & MASK) << 8);
	        result = result + ((b[2] & MASK) << 16);
	        result = result + ((b[3] & MASK) << 24);            
	    return result;
	}
	
	/**
	 * 返回对传入的字节数组的解码结果，这个结果会作为后续业务处理的输入参数。详见AbstractChannelHandler及其子类
	 * 
	 * @param ctx
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	public abstract Object parsePacket(ChannelHandlerContext ctx, byte[] packet)
			throws Exception;
	
	/**
	 * 返回对传入的字节解密后的结果，如果是没有加密的包，直接返回原数据即可
	 */
	public abstract byte[] decrypt(byte[] src);
}
