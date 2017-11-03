package com.jingye.coffeemac.common.adapter;

import java.util.HashSet;
import java.util.Set;


public class TAdapterHint extends BaseAdapterHint {
	private int maxHint;
	private Set<String> retains;
	private Set<String> predicts;
	
	public TAdapterHint(int max) {
		super();
		
		this.maxHint = max;
	}
	
	@Override
	protected void hintNow() {
		// hint bound
		int hint[] = getHint();
		int firstHint = hint[0];
		int lastHint = firstHint + hint[1];

		// max
		int max = adapter.getCount();
		
		// overflow
		if (lastHint > max) {
			lastHint = max;
		}
		
		// hint count
		int hintCount = lastHint - firstHint;
		
		// predict bound
		int firstPredict = firstHint;
		int lastPredict = lastHint;
		
		// predict count
		int predictCount = this.maxHint;
		if (predictCount > max) {
			predictCount = max;
		}

		if (predictCount > hintCount) {
			// extend
			int extend = (predictCount - hintCount) / 2;
			firstPredict -= extend;
			lastPredict += extend;
			
			// adjust left
			while (lastPredict > max) {
				firstPredict--;
				lastPredict--;
			}
			
			// adjust right
			while (firstPredict < 0) {
				firstPredict++;
				lastPredict++;
			}
			
			// ensure
			if (firstPredict < 0) {
				firstPredict = 0;
			}
			
			if (lastPredict > max) {
				lastPredict = max;
			}
		}
		
		// take hint ID
		Set<String> retains = takeHintIds(firstHint, lastHint, null, null);
		Set<String> predicts = takeHintIds(firstPredict, firstHint, null, retains);
		predicts = takeHintIds(lastHint, lastPredict, predicts, retains);
		
		// handle hint
		handleHint(this.retains, this.predicts, retains, predicts);
		
		// update
		this.retains = retains;
		this.predicts = predicts;
	}
	
	private Set<String> takeHintIds(int first, int last, Set<String> ids, Set<String> retains) {
		for (int position = first; position < last; position++) {
			// cache id
			String id = ((TListItem) adapter.getItem(position)).getHintId();
			
			// skip
			if (id == null) {
				continue;
			}
			
			// check retain
			if (retains != null && retains.contains(id)) {
				continue;
			}
			
			// take
			if (ids == null) {
				ids = new HashSet<String>();
			}
			
			ids.add(id);
		}
		
		return ids;
	}
	
	protected void handleHint(Set<String> lastRetains, Set<String> lastPredicts, Set<String> retains, Set<String> predicts) {
		
	}
}
