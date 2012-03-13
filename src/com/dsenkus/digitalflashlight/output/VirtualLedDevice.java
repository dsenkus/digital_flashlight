package com.dsenkus.digitalflashlight.output;

import android.graphics.drawable.Drawable;

public class VirtualLedDevice extends OutputDevice {
	
	protected Drawable drawable;
	
	protected Boolean state;
	
	protected Boolean started;
	
	public VirtualLedDevice(Drawable d){
		drawable = d;
		state = false;
		started = false;
	}
	
	public void start() {
		started = true;
	}

	public void stop() {
		setStateOff();
		state = false;
		started = false;
	}
	

	public void setStateOn() {
		if (started) {
			drawable.setLevel(1);
			state = true;
		}
	}

	public void setStateOff() {
		if (started) {
			drawable.setLevel(0);
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

}
