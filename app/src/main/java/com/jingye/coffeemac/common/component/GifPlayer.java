package com.jingye.coffeemac.common.component;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class GifPlayer {
	private static final int WHAT_lOOP = 1;
	
	private Context context;
	private TypegifOpenHelper helper;
	
	public interface Receiver {
		public void onFrame(Bitmap bitmap);
	}
	
	Receiver receiver;
	
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
	public GifPlayer(Context context) {
		this.context = context;
	}
	
	public void play(int resId) {
		InputStream is = null;
		
		try {
			is = context.getResources().openRawResource(resId);
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}
		
		if (is == null) {
			return;
		}
		
		prepare(is);
		
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		is = null;
		
		nextLoop(onLoop(false));
	}
	
	public void stop() {
		stopLoop();
		
		recycle();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_lOOP) {
				nextLoop(onLoop(true));
			}
		}
	};
	
	private void stopLoop() {
		handler.removeMessages(WHAT_lOOP);
	}
	
	private void nextLoop(int delay) {
		if (delay < 0) {
			return;
		}
		
		handler.sendEmptyMessageDelayed(WHAT_lOOP, delay);
	}
	
	private int onLoop(boolean next) {
		if (helper == null) {
			return -1;
		}
				
		Bitmap bitmap = next ? helper.nextBitmap() : helper.getFrame(0);
		
		if (bitmap == null) {
			return -1;
		}
		
		if (receiver != null) {
			receiver.onFrame(bitmap);
		}
		
		return helper.nextDelay();
	}
	
	private void prepare(InputStream is) {
		helper = new TypegifOpenHelper();
		
		if (helper.read(is) != TypegifOpenHelper.STATUS_OK) {
			helper = null;
		}
	}
	
	private void recycle() {
		if (helper == null) {
			return;
		}

		for (int i = 0; i < helper.getFrameCount(); i++) {
			recycleBitmap(helper.getFrame(i));
		}
		
		helper = null;
		
		System.gc();
	}
	
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
