package org.usfirst.frc.team1807.robot;

public interface Constants {
	
	//PWM Ports
	public static int LF = 2;
	public static int LB = 3;
	public static int RB = 1;
	public static int RF = 0;
	public static int ELEVATOR = 5;
	public static int LEFTSOURCEA = 3;
	public static int LEFTSOURCEB = 2;
	public static int RIGHTSOURCEA = 0;
	public static int RIGHTSOURCEB = 1;
	
	//Strings
	public static final String NEW_RECORDING_NAME = "test";
	public static final String SAVED_NAME = "test";
	public static final String recordingAuto = "Teleop Recorded";
	public static final String leftAuto = "Left";
	public static final String rightAuto = "Right";
	public static final String centerAuto = "Center";
	public static final String doLine = "Cross Line";
	public static final String noLine = "No Line";
	public static final String DONTMOVE = "Don't fucking move";
	
	//
	public static int manipPort = 0;
	public static int joyPort = 1;
	public static int COMPRESSOR_CAN = 0;
	
	//Analog ports
	public static int A_PSENSE = 3;
	public static int A_POT = 1;
	
	//Degrees and lengths
	public static double SWITCH = 0;
	public static double SCALE = 0;
	public static double outerPer90 = 7;
	public static double innerPer90 = -7;
	public static double top = 0;
	public static double bottom = 0;
	//DIO Ports
	public static int D_ULTRA1_PING = 1;
	public static int D_ULTRA1_ECHO = 2;
	public static int D_ULTRA2_PING = 3;
	public static int D_ULTRA2_ECHO = 4;
	
	//Buttons
}
