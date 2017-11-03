package com.jingye.coffeemac.service.protocol.pack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.jingye.coffeemac.service.protocol.LinkFrame;
import com.jingye.coffeemac.service.protocol.PacketHeader;
import com.jingye.coffeemac.service.protocol.PacketHeaderConvertException;
import com.jingye.coffeemac.service.protocol.encrypt.RC4;
import com.jingye.coffeemac.service.protocol.request.Request;
import com.jingye.coffeemac.util.HexDump;
import com.jingye.coffeemac.util.log.LogUtil;


/**
 * 包预处理器
 * <p>
 * 加解密之类
 * </p>
 * User: jingege Date: 1/13/12 Time: 3:31 PM
 */
public class PackagePacker {

	private final int PACKET_COMPRESS_SIZE = 1024;
	
	byte[] rc4Key = null;

	RC4 rc4Encrypt;

	RC4 rc4Decrypt;

	KeyPair rsaKeyPair = null;

	/*
	 * 序列号在部分协议里,被用于判断是否是离线的标记.0是离线1是在线的消息, 所以正常的serialid从2开始比较正常
	 */
	private volatile short serialId = 2;

	public PackagePacker() {
		initRsaKeyPair();
	}

	public void initRsaKeyPair() {
		// e=0x10001 by default
		int keySize = 1024;
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		kpg.initialize(keySize);
		rsaKeyPair = kpg.generateKeyPair();
	}

	/**
	 * unpack请求
	 * <p>
	 * 写入之前预处理
	 * </p>
	 * 
	 * @param request
	 * @return
	 */
	public Pack packRequest(Request request) {
		LinkFrame linkFrame = request.getLinkFrame();
		Pack body = request.packRequest();
		Pack pack = new Pack();
		// 填充序列号
		//linkFrame.serialId = this.getSerialId();

		int bodySize = body.size();
		ByteBuffer actBuffer = null;
		// 判断是否对包进行压缩
		if (bodySize < PACKET_COMPRESS_SIZE
				|| linkFrame.isCompressed()) {
			actBuffer = body.getBuffer();
		} else {
			actBuffer = PacketCompressor.compress(body.getBuffer());
			bodySize = actBuffer.limit();
			linkFrame.setCompressed();
		}
		
		try {
			PacketHeader header = linkFrame.toNewStyleHeader(false, false);
			header.setPacketLength(header.size() + bodySize);
			pack.putMarshallable(header);
			pack.putBuffer(actBuffer);

			LogUtil.core("send " + header);
			
			Pack ret = encrypt(pack);
			return ret;
		} catch (PacketHeaderConvertException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * unpck响应
	 * <p>
	 * 写入之前预处理
	 * </p>
	 * 
	 * @param responseBuffer
	 * @return
	 */
	public Unpack unpackResponse(ByteBuffer responseBuffer) {
		Unpack unpack = new Unpack(responseBuffer);
		unpack = this.decrypt(unpack);
		return unpack;
	}

	public int readLength(byte[] bytes) {
		assert bytes.length == 4;
		if (rc4Key == null) {
			return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
					.getInt();
		} else {
			Unpack unpack = new Unpack(bytes);
			unpack = decrypt(unpack);
			return unpack.popInt();
		}
	}

	private short getSerialId() {
		return this.serialId;
	}

	public void setRc4Key(byte[] rc4Key) {
		this.rc4Key = rc4Key;
		this.rc4Decrypt = new RC4(rc4Key);// Cipher.getInstance("RC4/ECB/NoPadding");
		// this.rc4Decrypt.init(Cipher.DECRYPT_MODE, rc4Key);

		this.rc4Encrypt = new RC4(rc4Key);// Cipher.getInstance("RC4/ECB/NoPadding");
		// this.rc4Encrypt.init(Cipher.ENCRYPT_MODE, rc4Key);
	}

	public Pack encrypt(Pack pack) {
		if (rc4Key == null) {
			LogUtil.vendor("RC4 key not inited");
			return pack;
		} else {
			LogUtil.vendor("RC4 key inited");
			ByteBuffer buffer = pack.getBuffer();
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);

			bytes = rc4Encrypt.rc4(bytes);

			Pack ret = new Pack();
			ret.putFetch(bytes);
			return ret;
		}

	}

	public Unpack decrypt(Unpack unpack) {
		if (rc4Key == null) {
			LogUtil.vendor("RC4 key not inited");
			return unpack;
		} else {
			LogUtil.vendor("RC4 key inited");
			ByteBuffer buffer = unpack.getBuffer();
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes, 0, buffer.remaining());
			bytes = rc4Decrypt.rc4(bytes);
			Unpack ret = new Unpack(ByteBuffer.wrap(bytes));
			return ret;
		}
	}

	public String getModulus() {
		KeyFactory kfactory = null;
		try {
			kfactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (rsaKeyPair == null) {
			return null;
		} else {
			RSAPublicKeySpec kpubspec = null;
			try {
				kpubspec = (RSAPublicKeySpec) kfactory.getKeySpec(
						rsaKeyPair.getPublic(), RSAPublicKeySpec.class);
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			return kpubspec.getModulus().toString(16);
		}
	}

	public byte[] decryptRc4Key(byte[] encrypted) {
		Cipher c = null;
		byte[] shortRc4Key = new byte[16];
		try {
			c = Cipher.getInstance("RSA/ECB/NoPadding");
			c.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
			byte[] rawRc4Key = c.doFinal(encrypted);
			LogUtil.vendor("Raw RC4 key[key=" + HexDump.toHex(rawRc4Key) + "]");
			// the last 16 bytes
			int length = (rawRc4Key.length > 16 ? 16 : rawRc4Key.length);
			System.arraycopy(rawRc4Key, rawRc4Key.length - length, shortRc4Key,
					16 - length, length);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		LogUtil.vendor("Real RC4 key[key=" + HexDump.toHex(shortRc4Key) + "]");
		return shortRc4Key;

	}
}
