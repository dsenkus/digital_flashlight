package com.dsenkus.digitalflashlight.widgets;

import com.dsenkus.digitalflashlight.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class DrawInstruction extends View {

	public static final int DEFAULT_COLOR = Color.WHITE;

	public static final int DEFAULT_ALPHA = 0x33;

	public static final int DEFAULT_HEIGHT = 15;

	public static final int DEFAULT_TEXT_SIZE = 14;

	public static final int HORIZONTAL = 0;

	public static final int VERTICAL_LEFT = 1;

	public static final int VERTICAL_RIGHT = 2;

	protected Paint mPaint = new Paint();

	// First Object Id
	protected int mObjectId1;

	// Second Object Id
	protected int mObjectId2;

	// Text
	protected String mText;

	// First Object View
	protected View mObject1;

	// Second Object View
	protected View mObject2;

	// Orientation
	protected int mOrientation;

	protected int mMarkingHeight;

	protected int mTextSize;

	int[] mDimensions = new int[4];

	public DrawInstruction(Context context) {
		this(context, null);
	}

	public DrawInstruction(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawInstruction(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// setup color
		mPaint.setColor(DEFAULT_COLOR);
		mPaint.setAlpha(DEFAULT_ALPHA);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DrawInstruction, defStyle, 0);

		// get attributes
		mObjectId1 = a.getResourceId(R.styleable.DrawInstruction_obj1, 0);
		mObjectId2 = a.getResourceId(R.styleable.DrawInstruction_obj2, 0);
		mText = a.getString(R.styleable.DrawInstruction_text);
		mOrientation = a.getInt(R.styleable.DrawInstruction_orientation,
				HORIZONTAL);
		mMarkingHeight = a.getInt(R.styleable.DrawInstruction_height,
				DEFAULT_HEIGHT);
		mTextSize = a.getInt(R.styleable.DrawInstruction_textSize,
				DEFAULT_TEXT_SIZE);

		a.recycle();
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (mDimensions[2] > 0 && mDimensions[3] > 0) {
			if (mOrientation == HORIZONTAL) {
				drawHorizontal(canvas);
			} else if (mOrientation == VERTICAL_LEFT
					|| mOrientation == VERTICAL_RIGHT) {
				drawVertical(canvas);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		attachObjects();
		getMeasuredDimensions(mDimensions);

		if (mDimensions[2] > 0 && mDimensions[3] > 0) {
			this.setMeasuredDimension(mDimensions[2] + 1, mDimensions[3] + 1);

		} else {
			setMeasuredDimension(0, 0);
		}
		invalidate();
	}

	public void drawVertical(Canvas canvas) {
		TextPaint tp = new TextPaint();
		tp.setARGB(DEFAULT_ALPHA, 0xff, 0xff, 0xff);
		tp.setTextSize(mTextSize);

		// ! solved improper alignment/text rotation problem
		tp.setSubpixelText(true);

		float textWidth = tp.measureText(mText);

		// +2 is random value that works well on Asus Transformer
		int textPosX = mMarkingHeight - mTextSize / 2 + 2;
		int textPosY = (int) ((int) (mDimensions[1] + (mDimensions[3] - mDimensions[1]) / 2) - textWidth / 2);

		// rotation guide lines
		// canvas.drawLine(rotationPx-10, rotationPy+10, rotationPx+10 ,
		// rotationPy-10, mPaint);
		// canvas.drawLine(rotationPx+10, rotationPy+10, rotationPx-10 ,
		// rotationPy-10, mPaint);

		canvas.save();

		if (mOrientation == VERTICAL_RIGHT) {
			int rotationPx = (mDimensions[2] + mDimensions[0]) / 2;
			int rotationPy = (mDimensions[3] + mDimensions[1]) / 2;
			canvas.rotate(180, rotationPx, rotationPy);
		}

		canvas.drawLine(mDimensions[0], mDimensions[1], mMarkingHeight,
				mDimensions[1], mPaint);
		canvas.drawLine(mDimensions[0], mDimensions[3], mMarkingHeight,
				mDimensions[3], mPaint);

		canvas.drawLine(mMarkingHeight, mDimensions[1], mMarkingHeight,
				textPosY - 5, mPaint);
		canvas.drawLine(mMarkingHeight, textPosY + textWidth + 5,
				mMarkingHeight, mDimensions[3], mPaint);

		canvas.rotate(90, textPosX, textPosY);
		canvas.drawText(mText, textPosX, textPosY, tp);
		canvas.restore();
	}

	public void drawHorizontal(Canvas canvas) {
		TextPaint tp = new TextPaint();
		tp.setARGB(DEFAULT_ALPHA, 0xff, 0xff, 0xff);
		tp.setTextSize(mTextSize);

		// ! solved improper alignment/text rotation problem
		tp.setSubpixelText(true);

		float textWidth = tp.measureText(mText);

		int textPosX = (int) ((int) (mDimensions[0] + (mDimensions[2] - mDimensions[0]) / 2) - textWidth / 2);

		// +2 is random value that works well on Asus Transformer
		int textPosY = mMarkingHeight + mTextSize / 2 - 2;

		canvas.drawLine(mDimensions[0], mDimensions[1], mDimensions[0],
				mMarkingHeight, mPaint);
		canvas.drawLine(mDimensions[2], mDimensions[1], mDimensions[2],
				mMarkingHeight, mPaint);

		canvas.drawLine(mDimensions[0], mMarkingHeight, textPosX - 5,
				mMarkingHeight, mPaint);
		canvas.drawLine(textPosX + textWidth + 5, mMarkingHeight,
				mDimensions[2], mMarkingHeight, mPaint);

		canvas.drawText(mText, textPosX, textPosY, tp);
	}
	
	protected void attachObjects(){
		Activity activity = (Activity) getContext();

		// isInEditMode necessary for GUI editor to work properly
		// it should always used when custom View tries to use Activity
		if (mObjectId1 > 0 && mObjectId2 > 0 && !isInEditMode()) {
			mObject1 = (View) activity.findViewById(mObjectId1);
			mObject2 = (View) activity.findViewById(mObjectId2);
		}

		if (mObject1 == null || mObject2 == null) {
			return;
		}

		ViewTreeObserver vto = mObject1.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				invalidate();
			}
		});

		ViewTreeObserver vto2 = mObject2.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				invalidate();
			}
		});		
	}

	protected void getMeasuredDimensions(int[] dimensions) {
		if (mObject1 == null || mObject2 == null) {
			return;
		}
		
		// get position
		int[] location1 = new int[2];
		mObject1.getLocationOnScreen(location1);

		// get position
		int[] location2 = new int[2];
		mObject2.getLocationOnScreen(location2);

		// get current Views position
		int[] locationOwn = new int[2];
		this.getLocationOnScreen(locationOwn);

		if (mOrientation == HORIZONTAL) {
			// get object 1 height
			int w1 = mObject1.getWidth();

			// get object 2 height
			int w2 = mObject2.getWidth();

			// adjust position difference between this view and objects
			int adjust = location1[0] - locationOwn[0];

			// calculate rectangle dimensions
			dimensions[0] = adjust + w1 / 2; // x0
			dimensions[1] = 0; // y0
			dimensions[2] = adjust + location2[0] + (w2 / 2) - location1[0]; // x1
			dimensions[3] = mMarkingHeight + mTextSize / 2; // y1

		} else if (mOrientation == VERTICAL_LEFT
				|| mOrientation == VERTICAL_RIGHT) {
			// get object 1 height
			int h1 = mObject1.getHeight();

			// get object 2 height
			int h2 = mObject2.getHeight();

			// adjust position difference between this view and objects
			int adjust = location1[1] - locationOwn[1];

			// calculate rectangle dimensions
			dimensions[0] = 0; // x0
			dimensions[1] = adjust + h1 / 2; // y0
			dimensions[2] = mMarkingHeight + mTextSize / 2; // x1
			dimensions[3] = adjust + location2[1] + (h2 / 2) - location1[1]; // y1
		}
	}

}
