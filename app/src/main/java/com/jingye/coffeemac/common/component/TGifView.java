package com.jingye.coffeemac.common.component;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jingye.coffeemac.helper.bitmaploader.BitmapLoader;

public class TGifView extends ImageView {
	
	
	public static final int GifView_android_src = 0;
	 public static final int[] GifView = {
         0x01010119
     };
	public static final int SHOW_ALL_FRAME = -1;
	public static final int SHOW_LAST_FRAME = -2;

	private TypegifOpenHelper mHelper;
	private Bitmap mBmp;
	private boolean mIsGif = true;		// 是否显示动画表情
	private boolean mShowLastFrame;		// 静态时，是否显示最后一帧
	private boolean mIsOneshit = false;	
	private int mShowFrame = SHOW_ALL_FRAME;
	
	private static final int WAHT_DRAW_FRAME = 1;
	
	
	private static class MyHandler extends Handler {
		MyHandler(TGifView gifView) {
			this.gifView = new WeakReference<TGifView>(gifView);
		}
		
		WeakReference<TGifView> gifView;
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != TGifView.WAHT_DRAW_FRAME) {
				return;
			}
			
			TGifView gifView = this.gifView.get();
			
			if (gifView == null) {
				return;
			}
			
			int delay = gifView.onPlay();
			
			
			if (delay >= 0) {
				sendEmptyMessageDelayed(WAHT_DRAW_FRAME, delay);
			}
		}
	};
	
	MyHandler mHandler = new MyHandler(this);
	
	public TGifView(Context context) {
		this(context, null);
	}
	
	public TGifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public TGifView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle);
        
        TypedArray a = context.obtainStyledAttributes(attrs,  GifView, defStyle, 0);
        int srcID = a.getResourceId(GifView_android_src, 0);  
        if(srcID > 0) {
        	setImageResource(srcID);
        	mHandler.sendEmptyMessage(WAHT_DRAW_FRAME);
        }
        a.recycle();
	}
	
	public void play(boolean play) {
		if (play) {
			mHandler.sendEmptyMessage(WAHT_DRAW_FRAME);
		} else {
			mHandler.removeMessages(WAHT_DRAW_FRAME);
		}
	}
	
	public int onPlay() {
		if (mHelper == null) {
			return -1;
		}
		
		mBmp = mHelper.nextBitmap();
		postInvalidate();
		
		return mHelper.nextDelay();
	}
	
	public void setLoop(boolean isOneshot) {
		mIsOneshit = isOneshot;
	}
	
	public void setImageStyle(boolean isGif) {
		mIsGif = isGif;
	}
	
	public void setShowFrame(int frame) {
		mShowFrame = frame;
	}
	
	@Override
	public void setImageResource(int resId) {
		if(mIsGif) {
			refresh(getResources().openRawResource(resId));
		} else {
//			Bitmap bmp = getStaticFrame(getResources().openRawResource(resId));
//			super.setImageBitmap(bmp);
//			recycle(); // 释放
		}
		super.setImageResource(resId);
	}
	
	public void setImageFilePath(String filePath) {
		if(mIsGif) {
			
		}
		super.setImageBitmap(BitmapLoader.getThumbnail(filePath));
	}	
	
	public void recycle() {
		if(mHelper == null) {
			return;
		}
	 
		for(int i = 0; i < mHelper.getFrameCount(); i++) {
			 recycleBitmap(mHelper.getFrame(i));
		}
		mHelper = null;
		System.gc();
	}
	public static boolean recycleBitmap(Bitmap bitmap) {
		if(bitmap == null || bitmap.isRecycled()) {
			return false;
		}
		bitmap.recycle();
		bitmap = null;
		return true;
	}
	private void refresh(InputStream is) {
	 
		recycle();
		mHandler.removeMessages(WAHT_DRAW_FRAME);
		mHelper = new TypegifOpenHelper();
		if(mHelper.read(is) != TypegifOpenHelper.STATUS_OK){
		 
			return;
		}
		
		mBmp = mHelper.getFrame(0); 
		postInvalidate();
		mHandler.sendEmptyMessageDelayed(WAHT_DRAW_FRAME, mHelper.nextDelay());
	}
	
	private Rect drawRect = new Rect();
	
	@Override
	protected void onDraw(Canvas canvas) {
		try {
			if(mIsGif) {
				getDrawingRect(drawRect);
				canvas.drawBitmap(mBmp, null, drawRect, null);
			} else {
				super.onDraw(canvas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
