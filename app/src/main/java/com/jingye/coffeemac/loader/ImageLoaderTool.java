package com.jingye.coffeemac.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoaderTool {

//	private static ImageLoader imageLoader = ImageLoader.getInstance();
//
//	public static ImageLoader getImageLoader(){
//		return imageLoader;
//	}
//
//	public static boolean checkImageLoader(){
//		return imageLoader.isInited();
//	}
//
//	public static void disPlay(String uri, ImageAware imageAware,int defaultPic){
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(defaultPic)
//		.showImageForEmptyUri(defaultPic)
//		.showImageOnFail(defaultPic)
//		.cacheInMemory(true)
//		.cacheOnDisc(true)
//		.bitmapConfig(Bitmap.Config.ARGB_8888)
//		.displayer(new SimpleBitmapDisplayer())
//		.build();
//
//		imageLoader.displayImage(uri, imageAware, options);
//	}

//	public static void disPlay(String uri, ImageView imageview, int defaultPic){
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(defaultPic)
//		.showImageForEmptyUri(defaultPic)
//		.showImageOnFail(defaultPic)
//		.cacheInMemory(true)
//		.cacheOnDisc(true)
//		.bitmapConfig(Bitmap.Config.ARGB_8888)
//		.displayer(new SimpleBitmapDisplayer())
//		.build();
//
//		imageLoader.displayImage(uri, imageview, options);
//	}

    public static void disPlay(Context context, String uri, ImageView imageView, int defaultPic) {
        Glide.with(context).load(uri).placeholder(defaultPic).error(defaultPic).into(imageView);
    }

//	public static void disPlay(String uri, ImageView imageview){
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//		.cacheInMemory(true)
//		.cacheOnDisc(true)
//		.bitmapConfig(Bitmap.Config.ARGB_8888)
//		.displayer(new SimpleBitmapDisplayer())
//		.build();
//
//		imageLoader.displayImage(uri, imageview, options);
//	}


//	public static void clear(){
//		imageLoader.clearMemoryCache();
////		imageLoader.clearDiscCache();
//	}
//
//
//	public static void resume(){
//		imageLoader.resume();
//	}
//
//	public static void pause(){
//		imageLoader.pause();
//	}
//
//	public static void stop(){
//		imageLoader.stop();
//	}
//
//	public static void destroy() {
//		imageLoader.destroy();
//	}

    public static void disPlayGif(Context context, String trimURL, ImageView iv) {
        Glide.with(context).load(trimURL).into(iv);
    }

    public static void disPlayLocalGif(Context context, int id , ImageView iv) {
        Glide.with(context).load(id).into(iv);
    }
}
