package com.jingye.coffeemac.util;

import java.util.UUID;

/**
 * 实现ID自增长：从0001开始，如果到9999了，重新从0001开始
 * @author yangwc
 *
 */
public class IDGenerater {
	
	static int i=0;
	
	static final int MAX_VALUE = 9999;
	
	public static String getNextId(){
		if(i>=MAX_VALUE){
			i = 0;
		}
		return padding(++i,4);
	}
	
	private static String padding(int value,int length){
		String valueString =  String.valueOf(value);
		for(int i=valueString.length();i<length;i++){
			valueString = "0"+valueString;
		}
		return valueString;
	}
	
	private static String getNextId(int i){
		if(i>=MAX_VALUE){
			i = 0;
		}
		return padding(++i,4);
	}
	
	/**
	 * 获取32位uuid
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String get36UUID(){
	    return UUID.randomUUID().toString();
	}
	
	public static void main(String []args){
		String a = getNextId(9998);
		com.jingye.coffeemac.util.log.LogUtil.vendor(a);
	}
}
