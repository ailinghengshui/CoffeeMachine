package com.jingye.coffeemac.service.protocol.filter;

import com.jingye.coffeemac.service.protocol.response.Response;

/**
 * 根据serviceid来捕获, 用于一类serviceid下各种command的包都是相同的协议,比如notify
 * 
 * @author Robby
 * 
 */
public class PacketServicIdFilter implements PacketFilter {
	public PacketServicIdFilter(short serviceId) {
		super();
		this.serviceId = serviceId;
	}

	private short serviceId;

	@Override
	public boolean accept(Response response) {

		return response.getLinkFrame().serviceId == serviceId;
	}

}
