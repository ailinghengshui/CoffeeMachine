package com.jingye.coffeemac.service.protocol.marshal;

import com.jingye.coffeemac.service.protocol.pack.Pack;
import com.jingye.coffeemac.service.protocol.pack.Unpack;

public interface Marshallable {
	public abstract void marshal(Pack pack);

	public abstract void unmarshal(Unpack unpack);
}
