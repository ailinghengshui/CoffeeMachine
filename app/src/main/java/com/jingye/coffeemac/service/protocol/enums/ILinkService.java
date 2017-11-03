package com.jingye.coffeemac.service.protocol.enums;

public interface ILinkService {
	public interface CommandId {
		public static final short CID_EXCHANGE_KEY = 1; // 验证交换密钥
		/** 心跳 */
		public static final short CID_HEARTBEAT = 2;
		/** 请求心跳 */
		public static final short CID_REQUEST_HEARTBEAT = 3;
    }

	// 是否需要压缩包的阀值
	public static final int POPO_PACKET_COMPRESS_SIZE = 1024;
}
