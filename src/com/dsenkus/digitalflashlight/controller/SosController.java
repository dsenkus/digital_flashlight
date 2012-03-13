package com.dsenkus.digitalflashlight.controller;

import java.util.ArrayList;

import android.os.Handler;

import com.dsenkus.digitalflashlight.output.OutputDevice;
import com.dsenkus.digitalflashlight.utils.LcdDisplayController;

public class SosController extends OutputController {
	protected final int INCREMENT = 10;
	protected final int MIN_VALUE = 20;
	protected final int MAX_VALUE = 2000;
	public static final int TICK_DEFAULT = 300;
	
	public class SosRunner implements Runnable {
		public volatile boolean requestStop = false;
		public volatile boolean isRunning = false;
		public volatile int tick = 300;
		public volatile Handler handler;
		public volatile ArrayList<OutputDevice> devices;
		public volatile String message = "1010100011101110111000101010000000";

		@Override
		public void run() {
			if (isRunning)
				return;

			requestStop = false;
			isRunning = true;

			char[] messageArray = message.toCharArray();
			int pos = 0;
			int len = messageArray.length;

			while (!requestStop) {
				try {

					if (messageArray[pos] == '1') {
						handler.post(new Runnable() {
							@Override
							public void run() {
								for (OutputDevice d : devices) {
									d.setStateOn();
								}
							}
						});
					} else {
						handler.post(new Runnable() {
							@Override
							public void run() {
								for (OutputDevice d : devices) {
									d.setStateOff();
								}
							}
						});
					}
					Thread.sleep(tick);

					pos = (pos + 1) == len ? 0 : pos + 1;

				} catch (InterruptedException ex) {

				} catch (RuntimeException ex) {
					requestStop = true;
					// errorMessage =
					// "Error setting camera flash status. Your device may be unsupported.";
					throw (ex);
				}
			}

			isRunning = false;
			requestStop = false;
		}
	}

	protected int delay;

	protected int period;

	protected SosRunner runner;

	protected Thread runnerThread;
	
	protected int mRunnerTimeUnit;

	public SosController(LcdDisplayController lcd) {
		runner = new SosRunner();
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
		if (runnerThread != null && runnerThread.isAlive()) {
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
			if (runner.tick < MAX_VALUE){
				runner.tick += INCREMENT;
			}
			break;
		}
		setLcdText();
	}

	@Override
	public void decrease(int option_id) {
		switch (option_id) {
		case OPTION_ID_A:
			if (runner.tick > MIN_VALUE){
				runner.tick -= INCREMENT;
			}
			break;
		}
		setLcdText();
	}
	
	public void setTick(int time){
		runner.tick = time;
	}
	
	public int getTick(){
		return runner.tick;
	}

	public void setLcdText() {
		mLcdController.clearText();
		mLcdController.setLine(1, "Sos Mode");
		mLcdController.setLine(2, "Tick " + Integer.toString(runner.tick)
				+ " MS");
		mLcdController.setLineInfo(2, "A");
		mLcdController.displayText();
	}
}
