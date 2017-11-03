package com.jingye.coffeemac.common.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jingye.coffeemac.R;


public class LetterIndexView extends View {

	private OnTouchingLetterChangedListener listener;
	private String[] letters = null;
	private Paint mPaint;
	private float hitPointY;
	private boolean isHit;
	private int normalColor;
	private int touchColor;
	private Bitmap hitBitmap;
	private int stringArrayId = R.array.letter_list;
	public LetterIndexView(Context paramContext) {
		this(paramContext, null);
	}

	public LetterIndexView(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public LetterIndexView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		Paint localPaint1 = new Paint();
		this.mPaint = localPaint1;
		this.hitPointY = 0.0F;
		this.isHit = false;
		this.normalColor = Color.GRAY;
		this.touchColor = Color.WHITE;
		
		this.hitBitmap = ((BitmapDrawable) paramContext.getResources().getDrawable(R.drawable.ic_hit_point)).getBitmap();
		
		this.mPaint.setAntiAlias(true);
		this.mPaint.setTextAlign(Paint.Align.CENTER);
		this.mPaint.setColor(this.normalColor);

	//	TypedArray typedArray =  paramContext.obtainStyledAttributes(paramAttributeSet,R.styleable.LetterIndexView);
		//stringArrayId = typedArray.getResourceId(R.styleable.LetterListView_stringArrayId, R.array.letter_list);
		letters = paramContext.getResources().getStringArray(stringArrayId);
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener paramOnTouchingLetterChangedListener) {
		this.listener = paramOnTouchingLetterChangedListener;
	}
    public void setLetters(String[] letters)
    {
    	this.letters = letters;
    }
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		
		int lettersLen;
		int letterIndex;
		
		int action = paramMotionEvent.getAction();
		switch(action){
		case MotionEvent.ACTION_MOVE:
			this.hitPointY = paramMotionEvent.getY();
			if ((this.isHit) && (this.listener != null)) {
				lettersLen = this.letters.length;
				int height = getHeight();
				int letterHight = height / lettersLen;
				int lettersHight = letterHight * lettersLen;
				float paddingTop = (height - lettersHight) / 2;
				float tempY = paramMotionEvent.getY() - paddingTop;
				letterIndex = (int) (tempY / letterHight);
				if(letterIndex < 0){
					letterIndex = 0;
				}
				if(letterIndex > (lettersLen - 1)){
					letterIndex = lettersLen - 1;
				}
				String str = this.letters[letterIndex];
				listener.touchMove(str);
			}
			break;
		case MotionEvent.ACTION_DOWN:
			this.isHit = true;
			setBackgroundResource(R.drawable.ic_indexbar_bg);
			this.mPaint.setColor(this.touchColor);
			if (this.listener == null)
				break;
			this.listener.touchDown();
			break;
		case MotionEvent.ACTION_UP:
			this.isHit = false;
			setBackgroundColor(Color.TRANSPARENT);
			this.mPaint.setColor(this.normalColor);
			if (this.listener == null)
				break;
			this.listener.touchUp();
			break;
		}
		invalidate();
		return true;
	}

	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		int lettersLen = this.letters.length;
		int height = getHeight();
		int width = getWidth();
		int letterHight = height / lettersLen;
		int lettersHight = letterHight * lettersLen;
		float paddingTop = (height - lettersHight) / 2 + letterHight;
		float textSize = height * 5 / 6 / lettersLen;
		this.mPaint.setTextSize(textSize);
		int i = 0;
		while (i < lettersLen) {
			String str = this.letters[i];
			float halfWidth = width / 2;
			float letterPosY = letterHight * i + paddingTop;
			paramCanvas.drawText(str, halfWidth, letterPosY, this.mPaint);
			i += 1;
		}
		if (this.isHit) {
			int halfWidth = getWidth() / 2;
			int halfHitBitmapWidth = this.hitBitmap.getWidth() / 2;
			float paddingLeft = halfWidth - halfHitBitmapWidth;
			float halfHitBitmapHeight = this.hitBitmap.getHeight() / 2;
			float hitBitmapX = this.hitPointY - halfHitBitmapHeight;
			paramCanvas.drawBitmap(this.hitBitmap, paddingLeft, hitBitmapX, this.mPaint);
		}
	}

	public abstract interface OnTouchingLetterChangedListener {

		public abstract void touchDown();

		public abstract void touchMove(String paramString);

		public abstract void touchUp();
	}

}
