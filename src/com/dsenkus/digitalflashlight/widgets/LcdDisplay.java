package com.dsenkus.digitalflashlight.widgets;

import java.util.Arrays;

import com.dsenkus.digitalflashlight.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class LcdDisplay extends View {
	public final float DEFAULT_GLASS_COVER_WIDTH = (float) 0.8;
	
	public final float DEFAULT_GLASS_COVER_HEIGHT = (float) 0.8;
	
	public final int   DEFAULT_SCREEN_PADDING = 15;
	
	public final int MIN_TEXT_LINE_MARGIN = 5;
	
	public final String DEFAULT_TEXT_TYPE_FACE = "fonts/digitaldreamskew.ttf";
	
	public final int 	DEFAULT_LINE_NUMBER = 3;
	
	public final int MIN_LETTERS_PER_LINE = 13;
	
	protected int mHeight;
	
	protected int mWidth;
	
	protected Canvas mCanvas;
	
	protected Bitmap mBitmap;
	
	protected Drawable mLcdBackground;
	
	protected Drawable mLcdGlassCover;
	
	protected Typeface mTextTypeFace;
	
	protected String[] mLines;
	
	protected String[] mLineInfo;
	
	protected int mDefaultTextSize = 0;
	
	protected int mLineInfoTextSize = 0;
	
	protected TextPaint mDefaultText;
	
	protected TextPaint mInfoText;
	
	public LcdDisplay (Context context) {
		this(context, null);	
	}
	

	public LcdDisplay(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LcdDisplay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mLines = new String[DEFAULT_LINE_NUMBER];
		mLineInfo = new String[DEFAULT_LINE_NUMBER];
		clearText();
		
		Resources 		res = getResources();
		mLcdBackground = res.getDrawable(R.drawable.lcd_screen_background);
		mLcdGlassCover = res.getDrawable(R.drawable.lcd_screen_glass_cover);
		mTextTypeFace  = Typeface.createFromAsset(res.getAssets(), DEFAULT_TEXT_TYPE_FACE); 
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		mLcdBackground.setBounds(0, 0, mWidth, mHeight);
		mLcdGlassCover.setBounds(
				Math.round(mWidth-(mWidth*DEFAULT_GLASS_COVER_WIDTH)), 0,
				mWidth, Math.round(mHeight*DEFAULT_GLASS_COVER_HEIGHT));
		
		mLcdBackground.draw(canvas);
		drawLcdText(canvas);
		drawLineInfo(canvas);
		mLcdGlassCover.draw(canvas);
	}
	
	public void display(){
		invalidate();
	}
	
	public void setLine(int line, String text){	
		if (line > 0 && line <= DEFAULT_LINE_NUMBER){
			mLines[line-1] = text;
		}
	}
	
	public void setLineInfo(int line, String text){	
		if (line > 0 && line <= DEFAULT_LINE_NUMBER){
			mLineInfo[line-1] = text;
		}
	}
	
	protected void drawLineInfo(Canvas canvas) {
		for (int i=0; i < DEFAULT_LINE_NUMBER; i++){
			int modePosY = getTextPositionY(i+1, mDefaultTextSize) - (mDefaultTextSize-mLineInfoTextSize)/2; 
			int modePosX = (int) (mWidth-DEFAULT_SCREEN_PADDING-mInfoText.measureText("A"));
			canvas.drawText(mLineInfo[i], modePosX, modePosY, mInfoText);
		}
	}
	
	protected void drawLcdText(Canvas canvas) {
		for (int i=0; i < DEFAULT_LINE_NUMBER; i++){
			canvas.drawText(mLines[i], 
					DEFAULT_SCREEN_PADDING, 
					getTextPositionY(i+1,mDefaultTextSize), mDefaultText);
		}
	}

	protected int getTextPositionY(int line, int textSize) {
		return DEFAULT_SCREEN_PADDING + textSize + ((mHeight - DEFAULT_SCREEN_PADDING*2) / 3)*(line-1);
//		if (line == 1) {
//			return textSize;
//		} else if (line == DEFAULT_LINE_NUMBER) {
//			return 100; //mHeight - textSize/2;// - DEFAULT_SCREEN_PADDING;
//		} else if (line == 2) {
//			return mHeight/2;
//		} else {
//			// does not work properly
//			return mHeight; // DEFAULT_SCREEN_PADDING
//					//+ ((mHeight - DEFAULT_SCREEN_PADDING*2) / 3) * line;				
//		}
	
	}

	protected int getTextSize(TextPaint tp) {
		int maxSizeByHeight = (mHeight - (2 * DEFAULT_SCREEN_PADDING) 
				- DEFAULT_LINE_NUMBER * 2 // text has 1px margin
				- (DEFAULT_LINE_NUMBER - 1) * MIN_TEXT_LINE_MARGIN)
				/ DEFAULT_LINE_NUMBER;
		
		int size = maxSizeByHeight;
		String testWord = getStringWithLengthAndFilledWithCharacter(MIN_LETTERS_PER_LINE, '#');
		
		int availableWidth = mWidth - DEFAULT_SCREEN_PADDING * 3; // 2 for paddings and 1 for info text
		
		while (tp.measureText(testWord) > availableWidth){
			size -= 1;
			tp.setTextSize(size);
		}
		
		return size;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
	    mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
	    
	    setTextStyles();
	    
	    setMeasuredDimension(mWidth, mHeight);
	}
	
	protected void setTextStyles(){
		// set default lcd text style
		mDefaultText = new TextPaint();;
		mDefaultText.setARGB(0xff, 0xff, 0xff, 0xff);
		mDefaultText.setTypeface(mTextTypeFace);
		mDefaultText.setTextSize(mDefaultTextSize);
		mDefaultText.setSubpixelText(true);
		mDefaultText.setAntiAlias(true);
		mDefaultText.setShadowLayer((float) 4.0, (float) 0.0, (float)  0.0, 0xff8B59DA);
		mDefaultTextSize = getTextSize(mDefaultText);
		mDefaultText.setTextSize(mDefaultTextSize);
		
		// set line info text style
		mInfoText = new TextPaint();
		mInfoText.setARGB(0xff, 0xff, 0xff, 0xff);
		mInfoText.setSubpixelText(true);
		mInfoText.setAntiAlias(true);
		mInfoText.setShadowLayer((float) 4.0, (float) 0.0, (float)  0.0, 0xff8B59DA);	
		mLineInfoTextSize =  (int) Math.floor((double) mDefaultTextSize*0.7);
		mInfoText.setTextSize(mLineInfoTextSize);
	}


	public void clearText() {
		for (int i=0; i < DEFAULT_LINE_NUMBER; i++){
			mLines[i] = "";
			mLineInfo[i] = "";
		}
		
	}
	
	protected String getStringWithLengthAndFilledWithCharacter(int length, char charToFill) {
		  if (length > 0) {
		    char[] array = new char[length];
		    Arrays.fill(array, charToFill);
		    return new String(array);
		  }
		  return "";
		}

}
