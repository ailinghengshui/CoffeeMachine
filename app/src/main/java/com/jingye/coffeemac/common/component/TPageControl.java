package com.jingye.coffeemac.common.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;


/**
 * @author zhousq
 *
 */
public class TPageControl  extends TabHost {

	private OnShouldTabChangeListener mListener;
	
	public TPageControl(Context context) {
		super(context);
	}
	
    public TPageControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
 
	public void setCurrentTab(int index) {
		 
        if (index < 0 || index >= getTabWidget().getTabCount()) {
            return;
        }

        if (index == getCurrentTab()) {
            return;
        }
        
        if(mListener != null){
        	mListener.onShouldTabChanged(getCurrentTabTag());
        }
        
		super.setCurrentTab(index);
	}
	
	public void setShouldTabChangeListener(OnShouldTabChangeListener listener){
		mListener = listener;
	}
	
    public interface OnShouldTabChangeListener {
        
    	void onShouldTabChanged(String tabId);
    }
}
