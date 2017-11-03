package com.jingye.coffeemac.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Locale;

public class EncryptUtil {
	
	public static String doEncryptByEncryptType(String encryptType,String source, String key){
	    return doEncryptByCustomize(source,key);
	}

	public static String doUnEncryptByEncryptType(String encryptType,String source, String key){
	    return doUnEncryptByCustomize(source,key);
	}

	
	public static String doEncryptByCustomize(String source, String key) {
		byte[] sourceBuf = null;
		try {
			sourceBuf = source.getBytes("UTF-8");//可能有汉字，故utf-8编码之(接口文档要求)
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte [] keyBuf = key.getBytes();
		int sourceBufLen = sourceBuf.length;
		int keyBufLen = keyBuf.length;
        String stmp="";
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < sourceBufLen; i++) {
			sourceBuf[i] = (byte)(sourceBuf[i]^keyBuf[i % keyBufLen]);//和key的对应字节求异或
			stmp=(Integer.toHexString(sourceBuf[i] & 0XFF));//将异或后的sourceBuf[i]转成16进制
            stmp.toUpperCase(Locale.getDefault());//转换成大写
            if (stmp.length()==1) {
            	result.append("0").append(stmp);
            }else {
            	result.append(stmp);
            }
		}

		return result.toString();
	}

	public static String doUnEncryptByCustomize(String source, String key) {
		byte [] sourceBuf = hexStr2Bytes(source);
		byte [] keyBuf = key.getBytes();
		int sourceBufLen = sourceBuf.length;
		int keyBufLen = keyBuf.length;
		for(int i = 0; i < sourceBufLen; i++){
			sourceBuf[i] = (byte)(sourceBuf[i]^keyBuf[i % keyBufLen]);//和key的对应字节求异或
		}
		
		try {
			return new String(sourceBuf,"UTF-8");
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

    public static String byte2HexStr(byte[] b) {
        String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++) {
            stmp=(Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
        }
        return hs.toUpperCase();
    }
    
    private static byte[] hexStr2Bytes(String src) {
        int m=0,n=0;
        int l=src.length()/2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m=i*2+1;
            n=m+1;
            ret[i] = uniteBytes(src.substring(i*2, m),src.substring(m,n));
        }
        return ret;
    }
    
    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    public static String getMD5(String source) {
		try {
			byte[] btInput = source.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md.length; i++) {
				int val = ((int) md[i]) & 0xff;
				if (val < 16){
				    sb.append("0");
				}
				sb.append(Integer.toHexString(val));
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
  
    public static String encryptIm(String source,String encoding) throws UnsupportedEncodingException{
    	byte bs[] = source.getBytes(encoding);
    	int l = bs.length;
    	for(int i = 0; i < l; i++){
    		bs[i] = (byte)(bs[i]-1);
    	}
    	return new String(bs,encoding);
    }

    public static String unEncryptIm(String source,String encoding) throws UnsupportedEncodingException{
    	byte bs[] = source.getBytes(encoding);
    	int l = bs.length;
    	for(int i = 0; i < l; i++){
    		bs[i] = (byte)(bs[i]+(byte)1);
    	}
    	return new String(bs,encoding);
    }
    
}
