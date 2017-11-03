package com.jingye.coffeemac.helper.cache;

public class CaherFactory {
	
	public static final String IMAGE_CACHER  = "ImageCacher";

	public static Cacher getCacher(String type){
		if(type.equals(IMAGE_CACHER)){
			return ImageCacher.newInstance();
		}
		return null;
	}
}
