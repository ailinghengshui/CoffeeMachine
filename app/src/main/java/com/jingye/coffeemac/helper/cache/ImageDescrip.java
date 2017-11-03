package com.jingye.coffeemac.helper.cache;

import java.io.File;





import com.jingye.coffeemac.helper.cache.ImageCacher.ImageCallBack;

import android.graphics.Bitmap;

public class ImageDescrip {

	public static final String BIG_IMAGE = "big_image";
	
	public interface AsynImageLoader{
		public Bitmap loadImage(Object...objects);
		
		public boolean isCacheToMemery();
		
		public boolean isCacherToFile();
		
		public File cacheFile();
	}
	
	public int maxPixels = -1;
	public int minSide = -1;
	public String key;
	public String filePath;
	public String path;
	public String url;
	public String description; 
	public Bitmap bitmap;
	public Object[] params; 
	public AsynImageLoader imageLoader;
	public boolean canReadFromCache = true;
	public String userInfo;
	public ImageCallBack callBack;
	public boolean isNeedThreadLoad = true;
}
