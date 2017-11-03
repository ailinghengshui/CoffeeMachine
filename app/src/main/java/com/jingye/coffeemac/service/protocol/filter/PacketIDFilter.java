package com.jingye.coffeemac.service.protocol.filter;

import com.jingye.coffeemac.service.protocol.response.Response;

/**
 * 根据serialID来filter,大部分用于同步包的捕获
 * 
 * @author Robby
 * 
 */
public class PacketIDFilter implements PacketFilter {
	public PacketIDFilter(short packetId) {
		super();
		this.packetId = packetId;
	}

	private short packetId;

	@Override
	public boolean accept(Response response) {

		return response.getLinkFrame().serialId == packetId;
	}

}
