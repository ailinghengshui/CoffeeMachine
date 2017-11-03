package com.jingye.coffeemac.common.adapter;

import java.util.HashSet;
import java.util.Set;

import com.jingye.coffeemac.helper.cache.ImageCacher;




public class TAdapterCacheHint extends TAdapterHint {
	public TAdapterCacheHint(int max) {
		super(max);
	}

	@Override
	protected void handleHint(Set<String> lastRetains, Set<String> lastPredicts, Set<String> retains, Set<String> predicts) {
		Set<String> evictors = null;
		
		/**
		 * evict last predicts
		 */
		if (lastPredicts != null) {
			for (String id : lastPredicts) {
				boolean inRetain = retains != null && retains.contains(id);
				boolean inPredict = predicts != null && predicts.contains(id);
			
				// miss
				if (!inRetain && !inPredict) {
					if (evictors == null) {
						evictors = new HashSet<String>();
					}
					
					evictors.add(id);
				}
			}
		}
		
		/**
		 * evict last retains
		 */
		if (lastRetains != null) {
			for (String id : lastRetains) {
				boolean inRetain = retains != null && retains.contains(id);
				boolean inPredict = predicts != null && predicts.contains(id);
			
				// miss
				if (!inRetain && !inPredict) {
					if (evictors == null) {
						evictors = new HashSet<String>();
					}
					
					evictors.add(id);
				}
			}
		}
		
		if (retains != null) {
			hintRetains(retains);
		}
		
		if (evictors != null) {
			hintEvictors(evictors);
		}
	}
	
	private void hintEvictors(Set<String> evictors) {
		ImageCacher.newInstance().hintBigImageEvictors(evictors);
	}
	
	private void hintRetains(Set<String> evictors) {
		ImageCacher.newInstance().hintBigImageRetains(evictors);
	}
}
