package com.dsenkus.digitalflashlight;

import java.util.ArrayList;

import com.dsenkus.digitalflashlight.controller.OutputController;
import com.dsenkus.digitalflashlight.controller.SosController;
import com.dsenkus.digitalflashlight.controller.StrobeController;
import com.dsenkus.digitalflashlight.controller.TorchController;
import com.dsenkus.digitalflashlight.output.LedDevice;
import com.dsenkus.digitalflashlight.output.OutputDevice;
import com.dsenkus.digitalflashlight.output.ScreenDevice;
import com.dsenkus.digitalflashlight.output.VirtualLedDevice;
import com.dsenkus.digitalflashlight.utils.LcdDisplayController;
import com.dsenkus.digitalflashlight.widgets.LcdDisplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ToggleButton;
import android.widget.RadioGroup;;

public class MainActivity extends Activity implements
		RadioGroup.OnCheckedChangeListener {

	protected ArrayList<OutputDevice> mOutputDevices;
	protected LedDevice mLedDevice;
	protected VirtualLedDevice mVirtualLedDevice;
	protected ScreenDevice mScreenDevice;

	protected OutputController mOutputController;
	protected TorchController mTorchController;
	protected StrobeController mStrobeController;
	protected SosController mSosController;

	protected PopupWindow mPopupWindow;
	
	protected SharedPreferences settings;
	
	/** Components */
	protected ToggleButton mPowerButton;
	protected Button mScreenDeviceCloseButton;
	protected LcdDisplay mLcdDisplay;
	protected LcdDisplayController mLcdController;
	protected Button mSetupButtonA1;
	protected Button mSetupButtonA2;
	protected Button mSetupButtonB1;
	protected Button mSetupButtonB2;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		mLcdDisplay = (LcdDisplay) findViewById(R.id.lcd_display);
		mLcdController = new LcdDisplayController(mLcdDisplay);
				
		mSetupButtonA1 = (Button) findViewById(R.id.setupButtonA1);
		mSetupButtonA2 = (Button) findViewById(R.id.setupButtonA2);
		mSetupButtonB1 = (Button) findViewById(R.id.setupButtonB1);
		mSetupButtonB2 = (Button) findViewById(R.id.setupButtonB2);

		// set listener for mode buttons
		RadioGroup modeGroup = (RadioGroup) findViewById(R.id.modeButtons);
		modeGroup.setOnCheckedChangeListener(this);

		// set device switch listener
		RadioGroup outputGroup = (RadioGroup) findViewById(R.id.outputButtons);
		outputGroup.setOnCheckedChangeListener(this);

		// power button
		mPowerButton = (ToggleButton) findViewById(R.id.powerButton);
		

		// screenDevice close button
		// screenDeviceCloseButton = (Button) findViewById(R.id.buttonClose);
		// screenDeviceCloseButton.getBackground().setAlpha(50);


		// Setup PopupWindow used by ScreenDevice
		DisplayMetrics displaymetrics = new DisplayMetrics();
		// get current screen height and with
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int ht = displaymetrics.heightPixels;
		int wt = displaymetrics.widthPixels;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupWindow = new PopupWindow(inflater.inflate(R.layout.screen_device,
				null, false), wt, ht, true);
		
		// prepare output devices
		mLedDevice = new LedDevice();
		mScreenDevice = new ScreenDevice(getWindow(), getContentResolver() ,
				mPopupWindow, findViewById(R.id.mainLayout));
		mOutputDevices = new ArrayList<OutputDevice>();
		//mOutputDevices.add(mLedDevice);

		// prepare output controllers
		mTorchController = new TorchController(mLcdController);
		mStrobeController = new StrobeController(mLcdController);
		mSosController = new SosController(mLcdController);
		
		// load configuration
		settings = getPreferences(MODE_PRIVATE);
		mStrobeController.setDelay(settings.getInt("strobeDelay", StrobeController.DELAY_DEFAULT));
		mStrobeController.setFlash(settings.getInt("strobeFlash", StrobeController.FLASH_DEFAULT));
		mSosController.setTick(settings.getInt("sosTick", SosController.TICK_DEFAULT));
		
		// select default controller and device
		mOutputController = mTorchController;
		RadioButton torch = (RadioButton) findViewById(R.id.modeTorch);
		RadioButton led   = (RadioButton) findViewById(R.id.outputLed);
		RadioButton screen   = (RadioButton) findViewById(R.id.outputScreen);
		RadioButton both   = (RadioButton) findViewById(R.id.outputBoth);
		torch.setChecked(true);
		if (isLedAvailable()){
			led.setChecked(true);
		} else {
			screen.setChecked(true);
			led.setEnabled(false);
			both.setEnabled(false);
		}
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);     
//		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
//		WindowManager.LayoutParams attrs = getWindow().getAttributes();
//		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//		getWindow().setAttributes(attrs);
	}

	@Override
	public void onStop() {
		super.onStop();
		
		saveSettings();
		
		// stop led device
		mLedDevice.stop();
	}
	
	public boolean isLedAvailable(){
		return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	protected void saveSettings(){
		SharedPreferences.Editor editor = settings.edit();
		
		// save settings
		editor.putInt("strobeDelay", mStrobeController.getDelay());
		editor.putInt("strobeFlash", mStrobeController.getFlash());
		editor.putInt("sosTick", mSosController.getTick());
		editor.commit();
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// devices list are going to be changed so stopping all working devices
		stopFlashlight();		

		switch (checkedId) {
		case R.id.modeTorch:
			mSetupButtonA1.setEnabled(false);
			mSetupButtonA2.setEnabled(false);
			mSetupButtonB1.setEnabled(false);
			mSetupButtonB2.setEnabled(false);

			mOutputController = mTorchController;
			
			mTorchController.setLcdText();
			break;

		case R.id.modeSos:
			mSetupButtonA1.setEnabled(true);
			mSetupButtonA2.setEnabled(true);
			mSetupButtonB1.setEnabled(false);
			mSetupButtonB2.setEnabled(false);
				
			mOutputController = mSosController;

			mSosController.setLcdText();
			break;

		case R.id.modeStrobe:
			mSetupButtonA1.setEnabled(true);
			mSetupButtonA2.setEnabled(true);
			mSetupButtonB1.setEnabled(true);
			mSetupButtonB2.setEnabled(true);
			
			mOutputController = mStrobeController;

			mStrobeController.setLcdText();
			break;

		case R.id.outputLed:
			mOutputDevices.clear();
			mOutputDevices.add(mLedDevice);
			break;

		case R.id.outputBoth:
			mOutputDevices.clear();
			mOutputDevices.add(mLedDevice);
			mOutputDevices.add(mScreenDevice);
			break;

		case R.id.outputScreen:
			mOutputDevices.clear();
			mOutputDevices.add(mScreenDevice);
			break;
		}

		resetState();
	}

	public void flashlightButtonListener(View v) {
		resetState();
	}
	
	public void setupButtonListener(View v) {
		switch (v.getId()){
			case R.id.setupButtonA1:
				mOutputController.increase(OutputController.OPTION_ID_A);
				break;
			case R.id.setupButtonA2:
				mOutputController.decrease(OutputController.OPTION_ID_A);
				break;
			case R.id.setupButtonB1:
				mOutputController.increase(OutputController.OPTION_ID_B);
				break;
			case R.id.setupButtonB2:
				mOutputController.decrease(OutputController.OPTION_ID_B);
				break;
		}
	}

	public void screenDeviceCloseButtonListener(View v) {
		mPopupWindow.dismiss();
		stopFlashlight();
		mPowerButton.setChecked(false);
	}

	protected void resetState() {
		if (mPowerButton.isChecked()) {
			startFlashlight();
		} else {
			stopFlashlight();
		}
	}

	protected void startFlashlight() {
		if (mOutputController != null) {
			mOutputController.start(mOutputDevices);
		}
	}

	protected void stopFlashlight() {
		if (mOutputController != null) {
			mOutputController.stop(mOutputDevices);
		}
	}
}