package com.jingye.coffeemac.service.protocol.response;

import com.jingye.coffeemac.service.protocol.pack.Unpack;

public class SingleResponse extends Response {

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		unpack(unpack);
		return null;
	}

}
