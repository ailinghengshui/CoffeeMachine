package com.jingye.coffeemac.net.netty;

import org.jboss.netty.channel.ChannelHandlerContext;

public interface IPackParser 
{
	public Object parsePacket(ChannelHandlerContext ctx, byte[] packet) throws Exception ;
	public abstract byte[] decrypt(byte[] src);
}
