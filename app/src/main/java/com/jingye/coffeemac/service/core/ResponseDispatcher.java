package com.jingye.coffeemac.service.core;

import com.jingye.coffeemac.net.netty.NioResponse;
import com.jingye.coffeemac.service.handler.ResponseFactory;
import com.jingye.coffeemac.service.handler.ResponseHandler;
import com.jingye.coffeemac.service.protocol.LinkFrame;
import com.jingye.coffeemac.service.protocol.ResponseCode;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.service.protocol.response.Response;
import com.jingye.coffeemac.util.log.LogUtil;

public class ResponseDispatcher extends TaskExecutor {

	private static final String NAME = "Response";
	private static final int CORE = 1;
	private static final int MAX = 1;
	private static final int KEEP_ALIVE = 30 * 1000;
	
	public ResponseDispatcher() {
		super(NAME, CORE, MAX, KEEP_ALIVE);
	}
	
	/**
	 * dispatch packet
	 * @param packet packet
	 */
	public void dispatchPacket(NioResponse packet) {
		dispatchPacket(packet.header, packet.body);
	}

	/**
	 * dispatch packet
	 * @param header header
	 * @param body body
	 */
	private void dispatchPacket(LinkFrame header, Unpack body) {
		// validate
		if (!ResponseFactory.getInstance().existsResponse(header)) {
			LogUtil.d("core", "Response not registered, SID: "
					+ header.serviceId + " CID: " + header.commandId);

			return;
		}
		
		// dispatch
		dispatchPacket(header, body, ResponseFactory.getInstance().queryResponsePriority(header));
	}
	
	/**
	 * dispatch packet
	 * @param header header
	 * @param body body
	 * @param priority priority
	 */
	private void dispatchPacket(final LinkFrame header, final Unpack body, final Integer priority) {
		LogUtil.core("dispatch packet: priority " + priority + " " + header);
		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				handlePacket(header, body, priority);
			}			
		};
		
		if (priority == null) {
			execute(task);
		} else {
			execute(task, priority);
		}
	}
	
	/**
	 * handle packet
	 * @param header header
	 * @param body body
	 * @param priority priority
	 */
	private void handlePacket(LinkFrame header, Unpack body, Integer priority) {
		// instantiate response
		Response response = ResponseFactory.getInstance().newResponse(header);
		
		// check
		if (response == null) {
			// TODO
			// trace
			
			return;
		}

		// set header
		response.setLinkFrame(header);
		
		/**
		 * status
		 * 
		 * false when need unpack body
		 */
		boolean status = header.resCode != ResponseCode.RES_SUCCESS || body == null;
		
		/**
		 * embedded
		 */
		boolean embedded = false;
		LinkFrame header2 = null;
		Unpack body2 = null;
		
		/**
		 * unpack
		 */
		if (!status) {
			try {
				// unpack body
				body2 = response.unpackBody(body);
				
				// embedded
				embedded = (body2 != null);
				
				if (embedded) {
					// new header
					header2 = new LinkFrame();
					
					// pop header
					body2.popMarshallable(header2);					
				}
				
				// OK here
				status = true;
			} catch (Exception e) {
				e.printStackTrace();
				
				// TODO
				// trace
			}	
		}
		
		// validate
		if (status) {
			// trace 	
			LogUtil.core("handle packet: "  + header);
			
			// execute response
			executeResponse(response);
			
			if (embedded) {
				// handle packet again
				handlePacket(header2, body2, null);
			}
		}	
	}

	private void executeResponse(Response response) {
		ResponseHandler handler = ResponseFactory.getInstance().queryResponseHandler(response);
		
		if (handler != null) {
			handler.processResponse(response);
		}
	}
}
