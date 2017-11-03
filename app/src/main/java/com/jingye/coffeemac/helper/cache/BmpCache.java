package com.jingye.coffeemac.helper.cache;

import java.util.Set;

import android.graphics.Bitmap;

public class BmpCache {
	private static int CACHE_SIZE = 5 * 1024 * 1024;
	private static int CANDIDATE_SIZE = 1 * 1024 * 1024;
	
	class BmpLruCache extends LruCache<String, Bitmap> {
		private boolean recycle = true;
		
		public BmpLruCache(int maxSize) {
			super(maxSize);
		}
		
		public void recycleBitmap(boolean recycle) {
			this.recycle = recycle;
		}
		
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
        	// Bitmap.getByteCount in API 12
        	return bitmap.getRowBytes() * bitmap.getHeight();
        }
        
        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        	// recycle
        	if (recycle) {
        		oldValue.recycle();
        	}
        }
	}

	/** cache */
	BmpLruCache cache = new BmpLruCache(CACHE_SIZE);

	/** candidate */
	BmpLruCache candidate = new BmpLruCache(CANDIDATE_SIZE);

    public void put(String key, Bitmap bitmap) {
    	// after recover from miss, as candidate
    	candidate.put(key, bitmap);
    }
    
    public Bitmap get(String key) {
    	// query cache
    	Bitmap bitmap = cache.get(key);

    	// cache miss
    	if (bitmap == null) {
    		// query candidate
    		candidate.recycleBitmap(false);
    		bitmap = candidate.remove(key);
    		candidate.recycleBitmap(true);
    		
    		// candidate hit
    		if (bitmap != null) {
    			// put to cache
    			cache.put(key, bitmap);
    		}
    	}
    	
    	return bitmap;
    }
    
    public void evict(String key) {
    	// cache or candidate
    	if (cache.remove(key) == null) {
    		candidate.remove(key);
    	}
    }
    
    public void evict(Set<String> keys) {
    	for (String key : keys) {
    		evict(key);
    	}
    }

    public void evictAll() {
    	// both cache and candidate
    	cache.evictAll();
    	candidate.evictAll();
    }
    
    public void hintEvictors(Set<String> keys) {
    	evict(keys);
    }
    
    public void hintRetains(Set<String> keys) {

    }
}
