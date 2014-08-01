package com.training.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MusicLRCView extends TextView {
	private ArrayList<String> mWordsList = new ArrayList<String>();
	private Paint mLoseFocusPaint;
	private Paint mOnFocusePaint;
	private float mX = 0;
	private float mMiddleY = 0;
	private float mY = 0;
	private static final int DY = 50;
	private int mIndex = 0;
	private String mLRConDisplay = null;
	private MusicLRCHandler mLRCHandler;

	public MusicLRCView(Context context) throws IOException {
		super(context);
		init();
	}

	public MusicLRCView(Context context, AttributeSet attrs) throws IOException {
		super(context, attrs);
		init();
	}

	public MusicLRCView(Context context, AttributeSet attrs, int defStyle)
	throws IOException {
		super(context, attrs, defStyle);
		init();
	}
	
	public void initMusicLRC(String file) {
		mLRConDisplay = file;
		mLRCHandler.readLRC(mLRConDisplay);
		mWordsList = mLRCHandler.getWords();

		initPaint();
		mIndex = 0;
		this.invalidate();
	}
	
	public void showMusicLRC(String file) {
		mLRConDisplay = file;
		mLRCHandler.readLRC(mLRConDisplay);
		mWordsList = mLRCHandler.getWords();

		initPaint();
		this.invalidate();
	}

	private void initPaint() {
		int focusSize = MediaActivity.mWidth/16;
		int nonFocusSize = focusSize*7/10;
    	mLoseFocusPaint = new Paint();
		mLoseFocusPaint.setAntiAlias(true);
		mLoseFocusPaint.setTextSize(nonFocusSize);
		mLoseFocusPaint.setColor(Color.WHITE);
		mLoseFocusPaint.setTypeface(Typeface.SERIF);

		mOnFocusePaint = new Paint();
		mOnFocusePaint.setAntiAlias(true);
		mOnFocusePaint.setColor(Color.YELLOW);
		mOnFocusePaint.setTextSize(focusSize);
		mOnFocusePaint.setTypeface(Typeface.SANS_SERIF);
	}
	
	public void setCurrentLyricLine(int index) {
		mIndex = index;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.BLACK);
		Paint p = mLoseFocusPaint;
		p.setTextAlign(Paint.Align.CENTER);
		Paint p2 = mOnFocusePaint;
		p2.setTextAlign(Paint.Align.CENTER);

		if(mIndex >= mWordsList.size()) {
			return;
		}
		
		canvas.drawText(mWordsList.get(mIndex), mX, mMiddleY, p2);

		int alphaValue = 25;
		float tempY = mMiddleY;
		for (int i = mIndex - 1; i >= 0; i--) {
			tempY -= DY;
			if (tempY < 0) {
				break;
			}
			p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
			canvas.drawText(mWordsList.get(i), mX, tempY, p);
			alphaValue += 25;
		}
		alphaValue = 25;
		tempY = mMiddleY;
		for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
			tempY += DY;
			if (tempY > mY) {
				break;
			}
			p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
			canvas.drawText(mWordsList.get(i), mX, tempY, p);
			alphaValue += 25;
		}
		mIndex++;
	}

	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);

		mX = w * 0.5f;
		mY = h;
		mMiddleY = h * 0.3f;
	}

	@SuppressLint("SdCardPath")
	private void init() throws IOException {
		setFocusable(true);
		mLRCHandler = new MusicLRCHandler();
		initMusicLRC(mLRConDisplay);
	}
}
