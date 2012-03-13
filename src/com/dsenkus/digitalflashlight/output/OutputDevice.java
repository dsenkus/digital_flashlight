package com.dsenkus.digitalflashlight.output;

public abstract class OutputDevice {
	
	abstract public void start();
	
	abstract public void stop();
	
	abstract public void toggleState();
	
	abstract public void setStateOn();
	
	abstract public void setStateOff();
	
}
