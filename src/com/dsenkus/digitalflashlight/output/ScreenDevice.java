package com.dsenkus.digitalflashlight.output;

import com.dsenkus.digitalflashlight.R;

import android.content.ContentResolver;
import android.provider.Settings.SettingNotFoundException;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

public class ScreenDevice extends OutputDevice {
	protected PopupWindow screen;
	protected ContentResolver contentResolver;
	protected Window window;
	protected WindowManager.LayoutParams layoutParams;
	protected View attachView;
	protected View contentView;
	protected Boolean state;
	protected Boolean started;
	protected int curBrightnessValue;
	protected Button mPowerButton;

	public ScreenDevice(Window w, ContentResolver cr, PopupWindow pw, View v) {
		screen = pw;
		window = w;
		attachView = v;
		contentView = screen.getContentView();
		state = false;
		started = false;
		contentResolver = cr;
		layoutParams = window.getAttributes();
		mPowerButton = (Button) contentView.findViewById(R.id.buttonClose);
		
		// get current screen brightness
		try {
			curBrightnessValue = android.provider.Settings.System.getInt(contentResolver, 
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException ex){
			
		}
	}
	
	public void start() {
		if(!screen.isShowing()){
			screen.showAtLocation(attachView, Gravity.LEFT, 0, 0);
		}
		setMaxBrightness();
		started = true;
	}

	public void stop() {
		if(screen.isShowing()){
			screen.dismiss();
		}
		setDefaultBrightness();
		state = false;
		started = false;
	}

	public void setStateOn() {
		if (started) {
			contentView.setBackgroundColor(0xffffffff);
			mPowerButton.getBackground().setLevel(0);
			state = true;
		}
	}

	public void setStateOff() {
		if (started) {
			contentView.setBackgroundColor(0xff000000);
			mPowerButton.getBackground().setLevel(1);
			state = false;
		}
	}

	public void toggleState() {
		if (state) {
			setStateOn();
		} else {
			setStateOff();
		}
	}
	
	protected void setMaxBrightness() {
		layoutParams.screenBrightness = 1;
		window.setAttributes(layoutParams);
	}
	
	protected void setDefaultBrightness(){
		layoutParams.screenBrightness = curBrightnessValue;
		window.setAttributes(layoutParams);
	}
}
