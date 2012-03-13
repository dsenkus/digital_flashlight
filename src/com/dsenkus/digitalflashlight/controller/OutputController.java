package com.dsenkus.digitalflashlight.controller;

import java.util.ArrayList;

import com.dsenkus.digitalflashlight.output.OutputDevice;
import com.dsenkus.digitalflashlight.utils.LcdDisplayController;

public abstract class OutputController {
	
	public static final int OPTION_ID_A = 1;
	
	public static final int OPTION_ID_B = 2;
	
	public LcdDisplayController mLcdController;
	
	abstract public void start(ArrayList<OutputDevice> devices);
	
	abstract public void stop(ArrayList<OutputDevice> devices);
	
	public void increase(int option_id) {
		return;
	}
	
	public void decrease(int option_id) {
		return;
	}
}
