package com.jingye.coffeemac.service.protocol.response;

import com.jingye.coffeemac.service.protocol.ServiceID;
import com.jingye.coffeemac.service.protocol.enums.ILinkService;
import com.jingye.coffeemac.service.protocol.pack.Unpack;
import com.jingye.coffeemac.util.HexDump;
import com.jingye.coffeemac.util.log.LogUtil;

@ResponseID(service = ServiceID.SVID_LITE_LINK, command = { ILinkService.CommandId.CID_EXCHANGE_KEY
		+ "" })
public class HandshakeResponse extends Response {

	private byte[] codedRc4Key;

	@Override
	public Unpack unpackBody(Unpack unpack) throws Exception {
		this.codedRc4Key = unpack.popVarbin();
		LogUtil.vendor("rc4key= " + HexDump.toHex(this.codedRc4Key));
		return null;
	}

	public byte[] getCodedRc4Key() {
		return codedRc4Key;
	}

}