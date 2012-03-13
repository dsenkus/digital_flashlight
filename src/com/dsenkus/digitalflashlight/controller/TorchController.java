package com.dsenkus.digitalflashlight.controller;

import java.util.ArrayList;

import com.dsenkus.digitalflashlight.output.OutputDevice;
import com.dsenkus.digitalflashlight.utils.LcdDisplayController;

public class TorchController extends OutputController{
	
	public TorchController(LcdDisplayController lcd){
		mLcdController = lcd;
	}
	
	public void start(ArrayList<OutputDevice> devices){
		for (OutputDevice d : devices) {
			d.start();
			d.setStateOn();
		}
	}
	
	public void stop(ArrayList<OutputDevice> devices){
		for (OutputDevice d : devices) {
			d.stop();
		}
	}

	public void setLcdText() {
		mLcdController.clearText();
		mLcdController.setLine(1, "Torch Mode");
		mLcdController.displayText();
	}	
}
