package com.sharpaiexample;

import android.os.SystemClock;

public class MyTimer {
	private long lastTime = 0;
	private double deltaTime = 0.0; 
	
	MyTimer() {
	}
	
	public void Reset() {
		lastTime = SystemClock.uptimeMillis();
	}
	
	public void Actualize() {
		long newTime = SystemClock.uptimeMillis();
		deltaTime = (double)(newTime - lastTime) / 1000.0;
		lastTime = newTime;
	}
	
	public double getDelta() {
		return deltaTime;
	}
}
