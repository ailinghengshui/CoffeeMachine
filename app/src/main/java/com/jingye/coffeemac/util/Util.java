package com.jingye.coffeemac.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jingye.coffeemac.util.log.LogUtil;

public class Util {
	// 随机类
	private static Random random;

	// byte buffer
	private static ByteArrayOutputStream baos = new ByteArrayOutputStream();

	// string buffer
	private static StringBuilder sb = new StringBuilder();

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(String str, String nullValue) {
		if (isEmpty(nullValue))
			return isEmpty(str);
		if (str == null || str.trim().length() == 0
				|| str.trim().equals(nullValue)) {
			return true;
		} else {
			return false;
		}
	}

	// 16进制字符数组
	private static char[] hex = new char[] { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 把字节数组从offset开始的len个字节转换成一个unsigned int， 因为java里面没有unsigned，所以unsigned
	 * int使用long表示的， 如果len大于8，则认为len等于8。如果len小于8，则高位填0 <br>
	 * 改变了算法, 性能稍微好一点. 在我的机器上测试10000次, 原始算法花费18s, 这个算法花费12s.
	 * 
	 * @param in
	 *            字节数组.
	 * @param offset
	 *            从哪里开始转换.
	 * @param len
	 *            转换长度, 如果len超过8则忽略后面的
	 * @return
	 */
	public static long getUnsignedInt(byte[] in, int offset, int len) {
		long ret = 0;
		int end = 0;
		if (len > 8)
			end = offset + 8;
		else
			end = offset + len;
		for (int i = offset; i < end; i++) {
			ret <<= 8;
			ret |= in[i] & 0xff;
		}
		return (ret & 0xffffffffl) | (ret >>> 32);
	}

	/**
	 * 比较两个字节数组的内容是否相等
	 * 
	 * @param b1
	 *            字节数组1
	 * @param b2
	 *            字节数组2
	 * @return true表示相等
	 */
	public static boolean isByteArrayEqual(byte[] b1, byte[] b2) {
		if (b1.length != b2.length)
			return false;

		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i])
				return false;
		}
		return true;
	}

	/**
	 * 检查收到的文件MD5是否正确
	 * 
	 * @param file
	 *            收到的存在本地的文件
	 * @param md5
	 *            正确的MD5
	 * @return true表示正确
	 */
	public static boolean checkFileMD5(RandomAccessFile file, byte[] md5) {
		return compareMD5(getFileMD5(file), md5);
	}

	/**
	 * 判断IP是否全0
	 * 
	 * @param ip
	 * @return true表示IP全0
	 */
	public static boolean isIpZero(byte[] ip) {
		for (int i = 0; i < ip.length; i++) {
			if (ip[i] != 0)
				return false;
		}
		return true;
	}

	/**
	 * 检查收到的文件MD5是否正确
	 * 
	 * @param filename
	 * @param md5
	 * @return
	 */
	public static boolean checkFileMD5(String filename, byte[] md5) {
		return compareMD5(getFileMD5(filename), md5);
	}

	/**
	 * 计算文件的MD5，最多只计算前面10002432字节
	 * 
	 * @param filename
	 * @return
	 */
	public static byte[] getFileMD5(String filename) {
		try {
			RandomAccessFile file = new RandomAccessFile(filename, "r");
			byte[] md5 = getFileMD5(file);
			file.close();
			return md5;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 计算文件的MD5，最多只计算前面10002432字节
	 * 
	 * @param file
	 *            RandomAccessFile对象
	 * @return MD5字节数组
	 */
	public static byte[] getFileMD5(RandomAccessFile file) {
		try {
			file.seek(0);
			byte[] buf = (file.length() > 10002432) ? new byte[10002432]
					: new byte[(int) file.length()];
			file.readFully(buf);
			return MessageDigest.getInstance("md5").digest(buf);
		} catch (IOException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * 得到一个文件的MD5字符串形式
	 * 
	 * @param filename
	 *            文件名
	 * @return MD5字符串形式，小写。如果发生错误，返回null
	 */
	public static String getFileMD5String(String filename) {
		byte[] md5 = getFileMD5(filename);
		if (md5 == null)
			return null;

		sb.delete(0, sb.length());
		for (int i = 0; i < md5.length; i++) {
			String s = Integer.toHexString(md5[i] & 0xFF);
			if (s.length() < 2)
				sb.append('0').append(s);
			else
				sb.append(s);
		}
		return sb.toString().toUpperCase(Locale.getDefault());
	}

	/**
	 * 比较两个MD5是否相等
	 * 
	 * @param m1
	 * @param m2
	 * @return true表示相等
	 */
	public static boolean compareMD5(byte[] m1, byte[] m2) {
		if (m1 == null || m2 == null)
			return true;
		for (int i = 0; i < 16; i++) {
			if (m1[i] != m2[i])
				return false;
		}
		return true;
	}

	/**
	 * 根据某种编码方式得到字符串的字节数组形式
	 * 
	 * @param s
	 *            字符串
	 * @param encoding
	 *            编码方式
	 * @return 特定编码方式的字节数组，如果encoding不支持，返回一个缺省编码的字节数组
	 */
	public static byte[] getBytes(String s, String encoding) {
		try {
			return s.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			return s.getBytes();
		}
	}

	/**
	 * 根据缺省编码得到字符串的字节数组形式
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] getBytes(String s) {
		return getBytes(s, "UTF-8");
	}

	/**
	 * 对原始字符串进行编码转换，如果失败，返回原始的字符串
	 * 
	 * @param s
	 *            原始字符串
	 * @param srcEncoding
	 *            源编码方式
	 * @param destEncoding
	 *            目标编码方式
	 * @return 转换编码后的字符串，失败返回原始字符串
	 */
	public static String getString(String s, String srcEncoding,
			String destEncoding) {
		try {
			return new String(s.getBytes(srcEncoding), destEncoding);
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * 从buf的当前位置解析出一个字符串，直到碰到一个分隔符为止，或者到了buf的结尾
	 * <p>
	 * 此方法不负责调整buf位置，调用之前务必使buf当前位置处于字符串开头。在读取完成 后，buf当前位置将位于分隔符之后
	 * </p>
	 * <p>
	 * 返回的字符串将使用ECP缺省编码，一般来说就是GBK编码
	 * </p>
	 * 
	 * @param buf
	 *            ByteBuffer
	 * @param delimit
	 *            分隔符
	 * @return 字符串
	 */
	public static String getString(ByteBuffer buf, byte delimit) {
		baos.reset();
		while (buf.hasRemaining()) {
			byte b = buf.get();
			if (b == delimit)
				return getString(baos.toByteArray());
			else
				baos.write(b);
		}
		return getString(baos.toByteArray());
	}

	/**
	 * 从buf的当前位置解析出一个字符串，直到碰到了buf的结尾
	 * <p>
	 * 此方法不负责调整buf位置，调用之前务必使buf当前位置处于字符串开头。在读取完成 后，buf当前位置将位于buf最后之后
	 * </p>
	 * <p>
	 * 返回的字符串将使用ECP缺省编码，一般来说就是GBK编码
	 * </p>
	 * 
	 * @param buf
	 *            ByteBuffer
	 * @return 字符串
	 */
	public static String getString(ByteBuffer buf) {
		baos.reset();
		while (buf.hasRemaining()) {
			baos.write(buf.get());
		}
		return getString(baos.toByteArray());
	}

	/**
	 * 从buf的当前位置解析出一个字符串，直到碰到了buf的结尾或者读取了len个byte之后停止
	 * <p>
	 * 此方法不负责调整buf位置，调用之前务必使buf当前位置处于字符串开头。在读取完成 后，buf当前位置将位于len字节之后或者最后之后
	 * </p>
	 * <p>
	 * 返回的字符串将使用ECP缺省编码，一般来说就是GBK编码
	 * </p>
	 * 
	 * @param buf
	 *            ByteBuffer
	 * @return 字符串
	 */
	public static String getString(ByteBuffer buf, int len) {
		return getString(buf, len, "UTF-8");
	}

	public static String getString(ByteBuffer buf, int len, String encoding) {
		baos.reset();
		while (buf.hasRemaining() && len-- > 0) {
			baos.write(buf.get());
		}
		return getString(baos.toByteArray(), encoding);
	}

	/**
	 * 从buf的当前位置解析出一个字符串，直到碰到了delimit或者读取了maxLen个byte或者 碰到结尾之后停止
	 * <p>
	 * 此方法不负责调整buf位置，调用之前务必使buf当前位置处于字符串开头。在读取完成 后，buf当前位置将位于maxLen之后
	 * </p>
	 * <p>
	 * 返回的字符串将使用ECP缺省编码，一般来说就是GBK编码
	 * </p>
	 * 
	 * @param buf
	 *            ByteBuffer
	 * @param delimit
	 *            delimit
	 * @param maxLen
	 *            max len to read
	 * @return String
	 */
	public static String getString(ByteBuffer buf, byte delimit, int maxLen) {
		baos.reset();
		while (buf.hasRemaining() && maxLen-- > 0) {
			byte b = buf.get();
			if (b == delimit)
				break;
			else
				baos.write(b);
		}
		while (buf.hasRemaining() && maxLen-- > 0)
			buf.get();
		return getString(baos.toByteArray());
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, String encoding) {
		try {
			return new String(b, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b);
		}
	}

	/**
	 * 根据缺省编码将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 字符串
	 */
	public static String getString(byte[] b) {
		return getString(b, "UTF-8");
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            要转换的起始位置
	 * @param len
	 *            要转换的长度
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, int offset, int len,
			String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b, offset, len);
		}
	}

	/**
	 * 根据缺省编码方式将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            要转换的起始位置
	 * @param len
	 *            要转换的长度
	 * @return
	 */
	public static String getString(byte[] b, int offset, int len) {
		return getString(b, offset, len, "UTF-8");
	}

	/**
	 * 把字符串转换成int
	 * 
	 * @param s
	 *            字符串
	 * @param faultValue
	 *            如果转换失败，返回这个值
	 * @return 如果转换失败，返回faultValue，成功返回转换后的值
	 */
	public static int getInt(String s, int faultValue) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return faultValue;
		}
	}

	/**
	 * 把字符串转换成long
	 * 
	 * @param s
	 *            字符串
	 * @param faultValue
	 *            如果转换失败，返回这个值
	 * @return 如果转换失败，返回faultValue，成功返回转换后的值
	 */
	public static long getLong(String s, int radix, long faultValue) {
		try {
			return Long.parseLong(s, radix);
		} catch (NumberFormatException e) {
			return faultValue;
		}
	}

	/**
	 * 把字符串转换成int
	 * 
	 * @param s
	 *            字符串
	 * @param radix
	 *            基数
	 * @param faultValue
	 *            如果转换失败，返回这个值
	 * @return 如果转换失败，返回faultValue，成功返回转换后的值
	 */
	public static int getInt(String s, int radix, int faultValue) {
		try {
			return Integer.parseInt(s, radix);
		} catch (NumberFormatException e) {
			return faultValue;
		}
	}

	/**
	 * 检查字符串是否是整数格式
	 * 
	 * @param s
	 *            字符串
	 * @return true表示可以解析成整数
	 */
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 把字符串转换成char类型的无符号数
	 * 
	 * @param s
	 *            字符串
	 * @param faultValue
	 *            如果转换失败，返回这个值
	 * @return 如果转换失败，返回faultValue，成功返回转换后的值
	 */
	public static char getChar(String s, int faultValue) {
		return (char) (getInt(s, faultValue) & 0xFFFF);
	}

	/**
	 * 把字符串转换成byte
	 * 
	 * @param s
	 *            字符串
	 * @param faultValue
	 *            如果转换失败，返回这个值
	 * @return 如果转换失败，返回faultValue，成功返回转换后的值
	 */
	public static byte getByte(String s, int faultValue) {
		return (byte) (getInt(s, faultValue) & 0xFF);
	}

	/**
	 * @param ip
	 *            ip的字节数组形式
	 * @return 字符串形式的ip
	 */
	public static String getIpStringFromBytes(byte[] ip) {
		sb.delete(0, sb.length());
		sb.append(ip[0] & 0xFF);
		sb.append('.');
		sb.append(ip[1] & 0xFF);
		sb.append('.');
		sb.append(ip[2] & 0xFF);
		sb.append('.');
		sb.append(ip[3] & 0xFF);
		return sb.toString();
	}

	/**
	 * 从ip的字符串形式得到字节数组形式
	 * 
	 * @param ip
	 *            字符串形式的ip
	 * @return 字节数组形式的ip
	 */
	public static byte[] getIpByteArrayFromString(String ip) {
		byte[] ret = new byte[4];
		StringTokenizer st = new StringTokenizer(ip, ".");
		try {
			ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
		} catch (Exception e) {
		}
		return ret;
	}

	/**
	 * 判断IP是否相等
	 * 
	 * @param ip1
	 *            IP的字节数组形式
	 * @param ip2
	 *            IP的字节数组形式
	 * @return true如果两个IP相等
	 */
	public static boolean isIpEquals(byte[] ip1, byte[] ip2) {
		return (ip1[0] == ip2[0] && ip1[1] == ip2[1] && ip1[2] == ip2[2] && ip1[3] == ip2[3]);
	}

	/**
	 * 这个不是用于调试的，真正要用的方法
	 * 
	 * @param encoding
	 *            编码方式
	 * @return 编码方式的字符串表示形式
	 */
	public static String getEncodingString(char encoding) {
		switch (encoding) {
		case 0x8602:
			return "GBK";
		case 0x0000:
			return "ISO-8859-1";
		case 0x8603:
			return "BIG5";
		default:
			return "GBK";
		}
	}

	/**
	 * 在buf字节数组中的begin位置开始，查找字节b出现的第一个位置
	 * 
	 * @param buf
	 *            字节数组
	 * @param begin
	 *            开始未知索引
	 * @param b
	 *            要查找的字节
	 * @return 找到则返回索引，否则返回-1
	 */
	public static int indexOf(byte[] buf, int begin, byte b) {
		for (int i = begin; i < buf.length; i++) {
			if (buf[i] == b)
				return i;
		}
		return -1;
	}

	/**
	 * 在buf字节数组中的begin位置开始，查找字节数组b中只要任何一个出现的第一个位置
	 * 
	 * @param buf
	 *            字节数组
	 * @param begin
	 *            开始未知索引
	 * @param b
	 *            要查找的字节数组
	 * @return 找到则返回索引，否则返回-1
	 */
	public static int indexOf(byte[] buf, int begin, byte[] b) {
		for (int i = begin; i < buf.length; i++) {
			for (int j = 0; j < b.length; j++)
				if (buf[i] == b[j])
					return i;
		}
		return -1;
	}

	/**
	 * @return Random对象
	 */
	public static Random random() {
		if (random == null)
			random = new Random();
		return random;
	}

	/**
	 * @return 一个随机产生的密钥字节数组
	 */
	public static byte[] randomKey() {
		byte[] key = new byte[16];
		random().nextBytes(key);
		return key;
	}

	/**
	 * 从content的offset位置起的4个字节解析成int类型
	 * 
	 * @param content
	 *            字节数组
	 * @param offset
	 *            偏移
	 * @return int
	 */
	public static final int parseInt(byte[] content, int offset) {
		return ((content[offset++] & 0xff) << 24)
				| ((content[offset++] & 0xff) << 16)
				| ((content[offset++] & 0xff) << 8)
				| (content[offset++] & 0xff);
	}

	/**
	 * 从content的offset位置起的2个字节解析成char类型
	 * 
	 * @param content
	 *            字节数组
	 * @param offset
	 *            偏移
	 * @return char
	 */
	public static final char parseChar(byte[] content, int offset) {
		return (char) (((content[offset++] & 0xff) << 8) | (content[offset++] & 0xff));
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字符串，每个字节之间空格分隔，头尾无空格
	 */
	public static String convertByteToHexString(byte[] b) {
		if (b == null)
			return "null";
		else
			return convertByteToHexString(b, 0, b.length);
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            从哪里开始转换
	 * @param len
	 *            转换的长度
	 * @return 16进制字符串，每个字节之间空格分隔，头尾无空格
	 */
	public static String convertByteToHexString(byte[] b, int offset, int len) {
		if (b == null)
			return "null";

		// 检查索引范围
		int end = offset + len;
		if (end > b.length)
			end = b.length;

		sb.delete(0, sb.length());
		for (int i = offset; i < end; i++) {
			sb.append(hex[(b[i] & 0xF0) >>> 4]).append(hex[b[i] & 0xF])
					.append(' ');
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字符串，每个字节没有空格分隔
	 */
	public static String convertByteToHexStringWithoutSpace(byte[] b) {
		if (b == null)
			return "null";

		return convertByteToHexStringWithoutSpace(b, 0, b.length);
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            从哪里开始转换
	 * @param len
	 *            转换的长度
	 * @return 16进制字符串，每个字节没有空格分隔
	 */
	public static String convertByteToHexStringWithoutSpace(byte[] b,
			int offset, int len) {
		if (b == null)
			return "null";

		// 检查索引范围
		int end = offset + len;
		if (end > b.length)
			end = b.length;

		sb.delete(0, sb.length());
		for (int i = offset; i < end; i++) {
			sb.append(hex[(b[i] & 0xF0) >>> 4]).append(hex[b[i] & 0xF]);
		}
		return sb.toString();
	}

	/**
	 * 转换16进制字符串为字节数组
	 * 
	 * @param s
	 *            16进制字符串，每个字节由空格分隔
	 * @return 字节数组，如果出错，返回null，如果是空字符串，也返回null
	 */
	public static byte[] convertHexStringToByte(String s) {
		try {
			s = s.trim();
			StringTokenizer st = new StringTokenizer(s, " ");
			byte[] ret = new byte[st.countTokens()];
			for (int i = 0; st.hasMoreTokens(); i++) {
				String byteString = st.nextToken();

				// 一个字节是2个16进制数，如果不对，返回null
				if (byteString.length() > 2)
					return null;

				ret[i] = (byte) (Integer.parseInt(byteString, 16) & 0xFF);
			}
			return ret;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 把一个16进制字符串转换为字节数组，字符串没有空格，所以每两个字符 一个字节
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] convertHexStringToByteNoSpace(String s) {
		int len = s.length();
		byte[] ret = new byte[len >>> 1];
		for (int i = 0; i <= len - 2; i += 2) {
			ret[i >>> 1] = (byte) (Integer.parseInt(s.substring(i, i + 2)
					.trim(), 16) & 0xFF);
		}
		return ret;
	}

	/**
	 * 把ip的字节数组形式转换成字符串形式
	 * 
	 * @param ip
	 *            ip地址字节数组，big-endian
	 * @return ip字符串
	 */
	public static String convertIpToString(byte[] ip) {
		sb.delete(0, sb.length());
		for (int i = 0; i < ip.length; i++) {
			sb.append(ip[i] & 0xFF).append('.');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 从头开始（包含指定位置）查找一个字节的出现位置
	 * 
	 * @param ar
	 *            字节数组
	 * @param b
	 *            要查找的字节
	 * @return 字节出现的位置，如果没有找到，返回-1
	 */
	public static int findByteOffset(byte[] ar, byte b) {
		return findByteOffset(ar, b, 0);
	}

	/**
	 * 从指定位置开始（包含指定位置）查找一个字节的出现位置
	 * 
	 * @param ar
	 *            字节数组
	 * @param b
	 *            要查找的字节
	 * @param from
	 *            指定位置
	 * @return 字节出现的位置，如果没有找到，返回-1
	 */
	public static int findByteOffset(byte[] ar, byte b, int from) {
		for (int i = from; i < ar.length; i++) {
			if (ar[i] == b)
				return i;
		}
		return -1;
	}

	/**
	 * 从指定位置开始（包含指定位置）查找一个字节的第N次出现位置
	 * 
	 * @param ar
	 *            字节数组
	 * @param b
	 *            要查找的字节
	 * @param from
	 *            指定位置
	 * @param occurs
	 *            第几次出现
	 * @return 字节第N次出现的位置，如果没有找到，返回-1
	 */
	public static int findByteOffset(byte[] ar, byte b, int from, int occurs) {
		for (int i = from, j = 0; i < ar.length; i++) {
			if (ar[i] == b) {
				j++;
				if (j == occurs)
					return i;
			}
		}
		return -1;
	}

	/**
	 * 把一个char转换成字节数组
	 * 
	 * @param c
	 *            字符
	 * @return 字节数组，2字节大小
	 */
	public static byte[] convertCharToBytes(char c) {
		byte[] b = new byte[2];
		b[0] = (byte) ((c & 0xFF00) >>> 8);
		b[1] = (byte) (c & 0xFF);
		return b;
	}

	/**
	 * 从字节数组的指定位置起的len的字节转换成int型(big-endian)，如果不足4字节，高位认为是0
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            转换起始位置
	 * @param len
	 *            转换长度
	 * @return 转换后的int
	 */
	public static int getIntFromBytes(byte[] b, int offset, int len) {
		if (len > 4)
			len = 4;

		int ret = 0;
		int end = offset + len;
		for (int i = offset; i < end; i++) {
			ret |= b[i] & 0xFF;
			if (i < end - 1)
				ret <<= 8;
		}
		return ret;
	}

	/**
	 * 得到一个字节数组的一部分
	 * 
	 * @param b
	 *            原始字节数组
	 * @param offset
	 *            子数组开始偏移
	 * @param len
	 *            子数组长度
	 * @return 子数组
	 */
	public static byte[] getSubBytes(byte[] b, int offset, int len) {
		byte[] ret = new byte[len];
		System.arraycopy(b, offset, ret, 0, len);
		return ret;
	}

	/**
	 * 得到协议的字符串，for debug
	 * 
	 * @param p
	 * @return
	 */
	public static String getProtocolString(int p) {
		switch (p) {
		case 0x1:
			return "ECP_PROTOCOL_FAMILY_BASIC";
		default:
			return "";
		}
	}

	public static byte[] getBytes(ByteBuffer buf, int length) {
		byte[] bytes = new byte[length];
		buf.get(bytes);
		return bytes;
	}

	public static byte[] getToken(ByteBuffer buf) {
		// 令牌长度
		int len = buf.getChar();
		// 令牌
		byte[] token = new byte[len];
		buf.get(token);
		return token;
	}

	/**
	 * 验证是否是手机号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		boolean flag = false;
		String rex = "^1[3,5,8]\\d{9}$";
		if (str.matches(rex)) {
			flag = true;
		}
		return flag;
	}

	public static void gzbLog(String log) {
		LogUtil.w("gzb", log);
	}

	public static void cjxLog(String log) {
		LogUtil.w("cjx", log);
	}

	public static void cjxLog(String log, int code) {
		String string = log + code;
		LogUtil.w("cjx", string);
	}

	public static void cjxLog(String log, boolean b) {
		if (b) {
			LogUtil.w("cjx", log + ":true");
		} else {
			LogUtil.w("cjx", log + ":false");
		}

	}

	public static void cjxLog(String tag, String log) {
		LogUtil.w("cjx", tag + log);
	}
	
	public static void nybLog(String tag, String log) {
		LogUtil.w("nyb", tag + log);
	}

	/**
	 * 函数栈日志
	 * 
	 * @param log
	 */
	public static void stackLog(String log) {
		// LogUtil.v("cjx", "++++++++++" + log + "++++++++++");
	}

	public static void memoryLog() {
		Runtime rt = Runtime.getRuntime();
		String log = "<====total memory:"
				+ ((float) rt.totalMemory() / (1024 * 1024))
				+ ",used memory:"
				+ (((float) rt.totalMemory() - rt.freeMemory()) / (1024 * 1024))
				+ ",free memory:" + ((float) rt.freeMemory() / (1024 * 1024))
				+ "====>";
		LogUtil.w("cjx", log);
	}

	/**
	 * 判断字符串中是否只含空格和回车，如果为空也返回true
	 * 
	 * @param str
	 */
	public static boolean isStringOnlyContainSpaceAndEnter(String str) {
		if (null == str) {
			return true;
		}
		if (str.length() == 0) {
			return true;
		}
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!(ch == ' ' || ch == '\r' || ch == '\n')) {
				return false;
			}
		}
		return true;
	}

	public static String filterInvalidChar(String str) {
		String regEx = "[^a-z^A-Z^0-9^\u4e00-\u9fa5~!@#$%^&()_+\"/?':.',]+$";
//		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！￥…… ;（）——+|{}【】‘；：”“’。，、？《》]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();

	}

	public static String filterUnChAndEn(String str) {

		String regEx = "[^a-z^A-Z^0-9^\u4e00-\u9fa5]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();

	}
	public static boolean isALetter(String str) {

		String regEx = "[^a-zA-Z]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return !m.matches();

	}

	/**
	 * new一个string
	 * 
	 * @param string
	 */
	public static String newString(String string) {
		if (null == string) {
			return null;
		} else {
			return new String(string);
		}
	}
	

}
