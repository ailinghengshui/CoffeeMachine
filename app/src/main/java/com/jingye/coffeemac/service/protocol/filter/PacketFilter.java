package com.jingye.coffeemac.service.protocol.filter;

import com.jingye.coffeemac.service.protocol.response.Response;

public interface PacketFilter {
	 public boolean accept(Response response);
}
