package com.dsenkus.digitalflashlight.controller;

import java.util.ArrayList;

import android.os.Handler;

import com.dsenkus.digitalflashlight.output.OutputDevice;
import com.dsenkus.digitalflashlight.utils.LcdDisplayController;

public class StrobeController extends OutputController {	
	protected final int INCREMENT = 10;
	protected final int MIN_VALUE = 20;
	protected final int MAX_VALUE = 2000;
	public static final int FLASH_DEFAULT = 500;
	public static final int DELAY_DEFAULT = 500;

	public class StrobeRunner implements Runnable {
		public volatile boolean requestStop = false;
		public volatile boolean isRunning = false;
		public volatile int flash = 500;
		public volatile int delay = 500;
		public volatile Handler handler;
		public volatile ArrayList<OutputDevice> devices;

		@Override
		public void run() {
			if (isRunning)
				return;

			requestStop = false;
			isRunning = true;
	
			while(!requestStop)
	    	{
	    		try{
	    			// On
	    			if (!requestStop){
		    			handler.post(new Runnable(){
		    				@Override
		    				public void run(){
		    	    			for (OutputDevice d : devices) {
		    	    				d.setStateOn();
		    	    			}	    					
		    				}
		    			});
		        		Thread.sleep(flash);
	    			}
	        		
	    			if (!requestStop){
		        		// Off
		    			handler.post(new Runnable(){
		    				@Override
		    				public void run(){
		    	    			for (OutputDevice d : devices) {
		    	    				d.setStateOff();
		    	    			}	    					
		    				}
		    			});
		        		Thread.sleep(delay);
	    			}
	    		}
	    		catch(InterruptedException ex)
	    		{

	    		}
	    		catch(RuntimeException ex)
	    		{
	    			requestStop = true;
	    			//errorMessage = "Error setting camera flash status. Your device may be unsupported.";
	    			//throw(ex);
	    		}
	    	}
			
	    	isRunning = false;
	    	requestStop=false;
		}
	}

	protected int delay;

	protected int period;
	
	protected StrobeRunner runner;
	
	protected Thread runnerThread;

	public StrobeController(LcdDisplayController lcd) {
		runner = new StrobeRunner();
		mLcdController = lcd;
	}

	public void start(ArrayList<OutputDevice> devices) {
		Handler handler = new Handler();
		runner.handler = handler;
		runner.devices = devices;	
		
		for (OutputDevice d : devices) {
			d.start();
		}

		runnerThread = new Thread(runner);
		runnerThread.start();
	}

	public void stop(ArrayList<OutputDevice> devices) {
		runner.requestStop = true;
		if (runnerThread != null && runnerThread.isAlive()){
			runnerThread.interrupt();
		}
		for (OutputDevice d : devices) {
			d.stop();
		}
	}
	
	@Override
	public void increase(int option_id) {
		switch (option_id) {
		case OPTION_ID_A:
			if (runner.flash < MAX_VALUE) {
				runner.flash += INCREMENT;
			}
			break;
		case OPTION_ID_B:
			if (runner.delay < MAX_VALUE) {
				runner.delay += INCREMENT;
			}
			break;
		}
		setLcdText();
	}

	@Override
	public void decrease(int option_id) {
		switch (option_id) {
		case OPTION_ID_A:
			if (runner.flash > MIN_VALUE){
				runner.flash -= INCREMENT;
			}
			break;
		case OPTION_ID_B:
			if (runner.delay > MIN_VALUE){
				runner.delay -= INCREMENT;
			}
			break;
		}
		setLcdText();
	}
	
	public void setDelay(int time){
		runner.delay = time;
	}
	
	public void setFlash(int time){
		runner.flash = time;
	}
	
	public int getDelay(){
		return runner.delay;
	}
	
	public int getFlash(){
		return runner.flash;
	}

	public void setLcdText() {
		mLcdController.clearText();
		mLcdController.setLine(1, "Strobe Mode");
		mLcdController.setLine(2, "Flash " + Integer.toString(runner.flash)  +" MS");
		mLcdController.setLine(3, "Delay " + Integer.toString(runner.delay)  +" MS");
		mLcdController.setLineInfo(2, "A");
		mLcdController.setLineInfo(3, "B");
		mLcdController.displayText();
	}
}
