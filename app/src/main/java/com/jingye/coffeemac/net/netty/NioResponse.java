package com.jingye.coffeemac.net.netty;

import com.jingye.coffeemac.service.protocol.LinkFrame;
import com.jingye.coffeemac.service.protocol.pack.Unpack;


public class NioResponse {
	public LinkFrame header ;
	public Unpack body;
}
