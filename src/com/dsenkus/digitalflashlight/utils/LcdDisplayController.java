package com.dsenkus.digitalflashlight.utils;

import com.dsenkus.digitalflashlight.widgets.LcdDisplay;

public class LcdDisplayController {
	
	protected LcdDisplay mLcdDisplay;
	
	public LcdDisplayController(LcdDisplay lcdDisplay){
		mLcdDisplay = lcdDisplay;
	}
	
	public void setLine(int line, String text){
		mLcdDisplay.setLine(line, text);
	}
	
	public void setLineInfo(int line, String text){
		mLcdDisplay.setLineInfo(line, text);
	}
	
	public void displayText(){
		mLcdDisplay.display();
	}
	
	public void clearText(){
		mLcdDisplay.clearText();
	}
}
