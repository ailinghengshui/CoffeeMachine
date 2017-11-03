package com.jingye.coffeemac.service.protocol.filter;

import com.jingye.coffeemac.service.protocol.response.Response;

public class PacketOrFilter implements PacketFilter {

	private PacketFilter filter1;
	private PacketFilter filter2;

	public PacketOrFilter(PacketFilter filter1, PacketFilter filter2) {
		super();
		this.filter1 = filter1;
		this.filter2 = filter2;
	}

	@Override
	public boolean accept(Response response) {
		return filter1.accept(response) || filter2.accept(response);
	}

}
