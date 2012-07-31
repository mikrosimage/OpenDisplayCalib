package com.konicaminolta.cs200;


public class CS200 {
	public static final long SPOT_INDEX = 0; /*see EyeOne.h*/
	
	private static CS200 theInstance = new CS200();
	
	private CS200() { 
	    System.loadLibrary("Kmsecs200");
		System.loadLibrary("JCS200");
	}
	
	public static CS200 getInstance() {
		return theInstance; 
	}
	
	public native boolean isConnected() throws CS200Exception;
	public native String getSerialID() throws CS200Exception;
	public native int init() throws CS200Exception;
	
	public final static char OBSERVER_2degree = 0;
	public final static char OBSERVER_10degree = 1;
	public final static char COLORSPACE_Lxy = 0;
	public final static char COLORSPACE_Lupvp = 1;
	public final static char COLORSPACE_LDeltauv = 2;
	public final static char COLORSPACE_XYZ = 3;
	public final static char COLORSPACE_DominantWaveLength = 4;
	
	public native void calibrate(int observer, int colorspace) throws CS200Exception;
	public native void startMeasurement() throws CS200Exception;	
	public native void closeMeasurement() throws CS200Exception;	
//	public native float[/*3*/] getTriStimulus() throws CS200Exception;
	public native String getTriStimulus() throws CS200Exception;
	public native String getSyncAndFrequency()  throws CS200Exception;
	public native void setSyncAndFrequency(int sync, int frequency)  throws CS200Exception;
	public native String getSpeed()  throws CS200Exception;
	public native void setSpeed(int mode, int duration)  throws CS200Exception;

	
}
