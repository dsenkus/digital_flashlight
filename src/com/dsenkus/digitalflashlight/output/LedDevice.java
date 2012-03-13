package com.dsenkus.digitalflashlight.output;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

public class LedDevice extends OutputDevice{
	protected static final String ON = Parameters.FLASH_MODE_TORCH;
	protected static final String OFF = Parameters.FLASH_MODE_OFF;
	
	protected Camera camera;
	protected Parameters params;
	
	protected Boolean started;
	
	public LedDevice() {
		started = false;
	}
	
	public void start(){
		if (!started){
			camera = Camera.open();
			params = camera.getParameters();
			started = true;
		}
	}
	
	public void stop(){
		if (started){
			releaseCameraResource();
		}
		started = false;
	}
	
	public void setStateOn() {
		if (started) {
			try {
				params.setFlashMode(ON);
				camera.setParameters(params);
			} 
			catch (Exception e) {
				Log.e("Camera", "Could not set state " + ON);
			}
		}
	}

	
	public void setStateOff() {
		if (started) {
			try {
				params.setFlashMode(OFF);
				camera.setParameters(params);
			} 
			catch (Exception e) {
				Log.e("Camera", "Could not set state " + OFF);
			}
		}
	}
	
	public void toggleState(){
		if (getState().equals(ON)) {
			setStateOn();
		} else {
			setStateOff();
		}
	}
	
	protected String getState(){
		return params.getFlashMode();
	}
	
	protected void releaseCameraResource(){
		camera.release();
	}
}
