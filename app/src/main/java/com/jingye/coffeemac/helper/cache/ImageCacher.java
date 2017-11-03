package com.jingye.coffeemac.helper.cache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;







import com.jingye.coffeemac.helper.bitmaploader.BitmapLoader;
import com.jingye.coffeemac.helper.threadpool.ThreadPool;

import android.graphics.Bitmap;
import android.os.Message;

public class ImageCacher extends Cacher {

	private static ImageCacher imageCacher;
	public static final int MSG_DOWNLOAD_IMAGE_OK = 20000;
	public static final int MSG_DOWNLOAD_IAMGE_FAILED = 20001;

	private static final long MAX_CACHE_FILE_SIZE = 1024 * 1024 * 50;
	private static final long MIN_IMAGE_SIZE = 1000;//1K一下的图片视为下载失败

	public static String cachePath = getDefaultCachePath();
	
	public static Map<String, Object> imageCached = new Hashtable<String, Object>();
	
	public static Map<String,ImageDescrip> downloadQueueMap = new Hashtable<String,ImageDescrip>();

	public static BmpCache bmpCache = new BmpCache();
	
	private ImageCacher() {
		super();
	}

	public interface ImageCallBack {

		public void onImageDownloadSuccess(ImageDescrip imageDes);

		public void onImageDownloadFailed(ImageDescrip imageDes,
				String errorMessage);
	}
	
	/**
	 * load watcher
	 */
	public interface LoadWatcher {
		/**
		 * watch what
		 * 
		 * @return what to watch
		 */
		public String what();
		
		/**
		 * on watch
		 * 
		 * @param ok state
		 */
		public void onWatch(boolean ok);
	}
	
	/** load watchers */
	private List<LoadWatcher> mLoadWatchers = new ArrayList<LoadWatcher>();
	
	/**
	 * watch load
	 * 
	 * @param watcher
	 */
	public void watchLoad(LoadWatcher watcher) {
		if (watcher == null) {
			return;
		}
		
		mLoadWatchers.add(watcher);
	}
	
	/**
	 * un-watch load
	 * 
	 * @param watcher
	 */
	public void unWatchLoad(LoadWatcher watcher) {
		if (watcher == null) {
			return;
		}

		mLoadWatchers.remove(watcher);
	}
	
	/**
	 * notify load watchers
	 * 
	 * @param what
	 * @param ok
	 */
	private void notifyLoadWatchers(String what, boolean ok) {
		if (what == null || what.length() <= 0) {
			return;
		}
		
		/**
		 * iterate watchers with the same watch and remove
		 */
		Iterator<LoadWatcher> iterator = mLoadWatchers.iterator();
		while (iterator.hasNext()) {
			LoadWatcher watcher = iterator.next();
			if (what.equals(watcher.what())) {
				iterator.remove();
				watcher.onWatch(ok);
			}
		}
	}
	
	/**
	 * on load result
	 * @param desc
	 * @param ok
	 * @param cache
	 */
	private void onLoadResult(ImageDescrip desc, boolean ok, boolean cache) {				
		downloadQueueMap.remove(desc.key);
	
		// which
		boolean which = false;// = AsynImageView.USER_INFO.equals(desc.userInfo);
		
		if (ok) {
			// key and bitmap
			Bitmap bitmap = desc.bitmap;
			String key = desc.key;
			
			// validate
			if (key != null && key.length() > 0 && bitmap != null) {
				// put
				if (which) {
					addBigImageCache(desc.key, desc.bitmap);
				} else {
					imageCached.put(desc.key, desc.bitmap);
				}
			}
		}
		
		ImageCallBack callback = desc.callBack;
		if (callback != null) {
			if (ok) {
				callback.onImageDownloadSuccess(desc);
			} else {
				callback.onImageDownloadFailed(desc, desc.description);
			}
		}
		
		if (which) {
			// notify
			notifyLoadWatchers(desc.key, ok);
		}
	}
	
	private boolean hasLoader(ImageDescrip desc) {
		if (desc == null) {
			return false;
		}
		
		ImageDescrip.AsynImageLoader loader = desc.imageLoader;
		Object[] params = desc.params;
		
		if (loader == null || params == null) {
			return false;
		}
		
		return true;
	}

	private Bitmap invokeLoader(ImageDescrip desc) {
		ImageDescrip.AsynImageLoader loader = desc.imageLoader;
		Object[] params = desc.params;
		
		return loader.loadImage(params);
	}
	
	public boolean loadImage(ImageDescrip desc) {
		if (desc == null) {
			return true;
		}
		
		String key = desc.key;
		if (key == null || key.length() <= 0) {
			return true;
		}
		
		if (downloadQueueMap.containsKey(key)) {
			return true;
		}
		
		downloadQueueMap.put(key, desc);
		ThreadPool.getInstance().addTask(new ImageLoadTask(desc));
		
		return false;
	}
		
	class ImageLoadTask implements Runnable {
		private ImageDescrip desc;

		public ImageLoadTask(ImageDescrip desc) {
			this.desc = desc;
		}

		public void run() {
			if (desc == null) {
				return;
			}
			
			Bitmap bitmap = null;
			
			// 如果用户自定义了下载图片的方式，则调用用户的
			if (hasLoader(desc)) {
				bitmap = invokeLoader(desc);

				if (null != bitmap) {
					desc.bitmap = bitmap;	

					if (desc.imageLoader.isCacherToFile()) {
						File file = desc.imageLoader.cacheFile();
						if (null != file && saveToPath(file, bitmap)) {

						}
					}
					
					notifyLoadResult(desc, true, desc.imageLoader.isCacheToMemery());
				} else {
					notifyLoadResult(desc, false, false);
				}
				
				return;
			}
			
			if (desc.filePath != null) {
				try {
					bitmap = BitmapLoader.getThumbnail(desc.filePath, desc.minSide, desc.maxPixels);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (bitmap != null) {
					desc.bitmap = bitmap;
					
					notifyLoadResult(desc, true, true);
				} else {
					notifyLoadResult(desc, false, false);
				}
			} else {
				// 先从文件中读.
				if (null != desc.key) {
					String path = desc.path == null ? cachePath
							: desc.path;
					File cacheFile = new File(path, desc.key);

					if (cacheFile.exists()) {
//						BitmapFactory.Options opt = new BitmapFactory.Options();  
//						opt.inPreferredConfig = Bitmap.Config.RGB_565;
//						opt.inPurgeable  =true;
//						opt.inInputShareable = true;

						bitmap = BitmapLoader.getThumbnail(cacheFile.getPath(), desc.minSide, desc.maxPixels);
					}
					
					if (null != bitmap) {
						desc.bitmap = bitmap;

						notifyLoadResult(desc, true, true);
						
						return;
					}
				}
				// 如果文件读取失败则从网络下载
				if (bitmap == null && null != desc.url) {
					try {
						bitmap = getBitmapFromUrl(desc.url, desc.minSide, desc.maxPixels);
						if (null == bitmap)
							throw new Exception(
									"bitmap is null ,http request failed");
						// 存入本地再缓存在内存
						String path = desc.path == null ? cachePath
								: desc.path;
						String key = desc.key == null ? desc.url
								.substring(desc.url.lastIndexOf("/") + 1)
								: desc.key;
						File dir = new File(path);
						dir.mkdirs();
						// 清空缓存的策越，目前是大于100M就将缓存全部清空
						if (dir.length() > MAX_CACHE_FILE_SIZE)
							clearFileCache(path);
						File file = new File(path, key);
						if (saveToPath(file, bitmap)) {
							desc.bitmap = bitmap;
							
							notifyLoadResult(desc, true, true);
						}
						else{
							desc.description = "保存图片失败(可能原因图片无效)";
							
							notifyLoadResult(desc, false, false);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						desc.description = e.getMessage();
					
						notifyLoadResult(desc, false, false);
					}
				}	
			}
		}
	}

	private boolean saveToPath(File file, Bitmap bitmap) {
		boolean isSuccess = true;
		try {
			if (file.exists())
				return true;
			
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] imageInByte = stream.toByteArray();
			long length = imageInByte.length;
			com.jingye.coffeemac.util.log.LogUtil.vendor("length="+length);
			if(MIN_IMAGE_SIZE>=length){
				stream.close();
				return false;
			}
			FileOutputStream fos = new FileOutputStream(file);
			stream.writeTo(fos);
			fos.flush();
			stream.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}

	/**
	 * 清空文件缓存的策越，目前是全部清空
	 * 
	 * @param path
	 */
	private void clearFileCache(String path) {

		delFolder(path);
	}

	private void delFolder(String folderPath) {
		try {
			deleteAllFileInFolder(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			com.jingye.coffeemac.util.log.LogUtil.vendor("删除文件夹操作出错");
			e.printStackTrace();

		}
	}

	private void deleteAllFileInFolder(String folderPath) {
		File file = new File(folderPath);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (folderPath.endsWith(File.separator)) {
				temp = new File(folderPath + tempList[i]);
			} else {
				temp = new File(folderPath + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				deleteAllFileInFolder(folderPath + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(folderPath + "/" + tempList[i]);// 再删除空文件夹
			}
		}

	}

	public Bitmap getBitmap(ImageDescrip imageDescrip) {
		// TODO Auto-generated method stub

		if (null == imageDescrip)
			return null;
		String key = imageDescrip.key;
		// 从内存中读取
		if (null != key && imageDescrip.canReadFromCache) {
			Object obj = null;
			//if(AsynImageView.USER_INFO.equals(imageDescrip.userInfo)){
			//	 Bitmap bmp = getBigImageCache(key);
			//	 
			//	 if (bmp != null) {
			///		 return bmp;
			//	 }
			//}else{
				 obj =imageCached.get(key);
			//}
			if (null != obj && obj instanceof Bitmap)
				return (Bitmap) obj;
		}

		if(imageDescrip.isNeedThreadLoad){
			loadImage(imageDescrip);
		}
		
		return null;
	}

	public static ImageCacher newInstance() {
		if (null == imageCacher)
			imageCacher = new ImageCacher();
		return imageCacher;
	}
	
	private void notifyLoadResult(ImageDescrip desc, boolean ok, boolean cache) {
		Message msg = Message.obtain();
		msg.what = ok ? MSG_DOWNLOAD_IMAGE_OK : MSG_DOWNLOAD_IAMGE_FAILED;
		msg.obj = desc;
		msg.arg1 = ok && cache ? 1 : 0;
		sendMessage(msg);
	}

	@Override
	public void handleMessage(Message msg) {
		ImageDescrip desc = msg.obj == null ? null : (ImageDescrip) msg.obj;
		boolean ok = msg.what == MSG_DOWNLOAD_IMAGE_OK;
		boolean cache = msg.arg1 == 1;

		if (desc == null) {
			return;
		}
		
		onLoadResult(desc, ok, cache);
	}

	private Bitmap getBitmapFromUrl(String imgUrl, int minSide, int maxPixels) {
		URL url;
		Bitmap bitmap = null;
		try {
			url = new URL(imgUrl);
			InputStream is = url.openConnection().getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bitmap = BitmapLoader.getThumbnail(bis, minSide, maxPixels);
			bis.close();
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
		return bitmap;
	}
	
	public void removeKey(String key){
		imageCached.remove(key);
	}
	
	public void hintBigImageEvictors(Set<String> keys){
		bmpCache.hintEvictors(keys);
	}
	
	public void hintBigImageRetains(Set<String> keys){
		bmpCache.hintRetains(keys);
	}
	
	public void addBigImageCache(String key, Bitmap bmp) {
		bmpCache.put(key, bmp);
	}
	
	public void removeBigImageCache(String key) {
		bmpCache.evict(key);
	}
	
	public Bitmap getBigImageCache(String key) {
		return bmpCache.get(key);
	}
	
	public static void clearBigImageCache(){
		bmpCache.evictAll();
	}
}
