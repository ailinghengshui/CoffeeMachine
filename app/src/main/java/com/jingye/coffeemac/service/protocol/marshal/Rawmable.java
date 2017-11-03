package com.jingye.coffeemac.service.protocol.marshal;

import java.nio.ByteBuffer;

import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.pack.Unpack;


public class Rawmable implements Marshallable {
	private byte[] m_bytes;

	/*
	 * src [ position : limit ] -> bytes
	 */
	public Rawmable(ByteBuffer src) {
		int size = src.remaining();
		m_bytes = new byte[size];
		src.get(m_bytes, 0, size);
	}

	public Rawmable(byte[] bytes) {
		m_bytes = bytes;
	}

	public Rawmable() {
		m_bytes = new byte[0];
	}

	public void marshal(Pack p) {
		// 是否加上字节数组长度信息？ rhythm
		// 不考虑，因为Rawmable就是原始数据 俞甲子
		// p.putInt( m_bytes.length );
		p.putFetch(m_bytes);
	}

	public void unmarshal(Unpack p) {
		// 现在是将Unpack中剩余的字节都解出，是否要考虑数组长度？
		// 不考虑， 因为Rawmable就是原始数据 俞甲子
		// int size = p.popInt();

		ByteBuffer buf = p.getBuffer();
		int size = buf.remaining();
		m_bytes = p.popFetch(size);
	}

	public void setBytes(byte[] bytes) {
		this.m_bytes = bytes;
	}

	public byte[] getBytes() {
		return m_bytes;
	}
}
