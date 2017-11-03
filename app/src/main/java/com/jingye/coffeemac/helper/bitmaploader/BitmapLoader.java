package com.jingye.coffeemac.helper.bitmaploader;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapLoader {
	public static Bitmap getThumbnail(String imageFile) {
		return getThumbnail(imageFile, -1, -1);
	}

	public static Bitmap getThumbnail(byte[] data, int offset, int length){
		return getThumbnail(data,offset,length,-1,-1);
	}
	public static Bitmap getThumbnail(InputStream input){
		return getThumbnail(input,-1,-1);
	}
	
	/**
	 * 
	 * @param path file path

	 * @return ratio zero unknown, otherwise aspect ratio
	 */
	public static float getAspectRatio(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		
		int width = opts.outWidth;
		int height = opts.outHeight;
		
		if (width == 0 || height == 0) {
			return 0f;
		} else {
			return (float) width / (float) height;
		}
	}
	/**
	 * 以最优的方式从磁盘读取位图到内存
	 * @param imageFile  图片的路径
	 * @param minSideLength  目标bitmap的最小边长（-1忽略）
	 * @param maxNumOfPixels 目标bitmap的最大像素个数（-1忽略）
	 * @return bitmap
	 */
	public static Bitmap getThumbnail(String imageFile, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile, opts);
		makeBitmapFactoryOptions(opts,minSideLength, maxNumOfPixels,-1);
		try {
			return BitmapFactory.decodeFile(imageFile, opts);
		} catch (OutOfMemoryError err) {
			//在这里捕获oom异常
		}
		return null;
	}
	/**
	 *  以最优的方式从磁盘读取位图到内存(限制最大高度)
	 * @param imageFile
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap getThumbnail(String imageFile, int maxHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile, opts);
		makeBitmapFactoryOptions(opts,-1, -1,maxHeight);
		try {
			return BitmapFactory.decodeFile(imageFile, opts);
		} catch (OutOfMemoryError err) {
			//在这里捕获oom异常
		}
		return null;
	}
	/**
	 * 以最优的方式从字节数组读取位图到内存
	 * @param data 字节数组
	 * @param offset 起始偏移
	 * @param length 字节长度
	 * @param minSideLength 目标bitmap的最小边长（-1忽略）
	 * @param maxNumOfPixels 目标bitmap的最大像素个数（-1忽略）
	 * @return bitmap
	 */
	public static Bitmap getThumbnail(byte[] data, int offset, int length,int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length, opts);
		makeBitmapFactoryOptions(opts,minSideLength, maxNumOfPixels,-1);
		try {
			return BitmapFactory.decodeByteArray(data, offset, length, opts);
		} catch (OutOfMemoryError err) {
			//在这里捕获oom异常
			
		}
		return null;
	}
	/**
	 * 以最优的方式从输入流读取位图到内存
	 * @param input 输入流
	 * @param minSideLength 目标bitmap的最小边长（-1忽略）
	 * @param maxNumOfPixels 目标bitmap的最大像素个数（-1忽略）
	 * @return
	 */
	public static Bitmap getThumbnail(InputStream input,int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(input);
		makeBitmapFactoryOptions(opts,minSideLength, maxNumOfPixels,-1);
		try {
			return BitmapFactory.decodeStream(input,null, opts);
		} catch (OutOfMemoryError err) {
			//在这里捕获oom异常
			
		}
		return null;
	}
	
	private static void makeBitmapFactoryOptions(BitmapFactory.Options opts,int minSideLength, int maxNumOfPixels,int maxHeight){
		opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels,maxHeight);
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inPurgeable  =true;
		opts.inInputShareable = true;
		opts.inJustDecodeBounds = false;
	}

	//根据要显示的大小和图片的实际大小计算采样率
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels,int maxHeight) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels,maxHeight);
		int roundedSize;
		//变成2的倍数处理
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;  
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels,int maxHeight) {
		double w = options.outWidth;
		double h = options.outHeight;
		double scale =  w/h;
		if(-1 != maxHeight){
			maxNumOfPixels = (int) (h>maxHeight?maxHeight*scale*maxHeight:h*w);
		}
			
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}

	}
}
