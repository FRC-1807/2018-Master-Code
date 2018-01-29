/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1807.robot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main, what will be finalized code for the 2018 FIRST Power Up Game.
 * @author - Joey, Peter and Friends
 * @since - we been stunting on niggas
 */
public class Robot extends IterativeRobot {
	
	private static final String NEW_RECORDING_NAME = "test";
	private static final String SAVED_NAME = "test";
	private static final String recordingAuto = "Teleop Recorded";
	private static final String leftAuto = "Left";
	private static final String rightAuto = "Right";
	private static final String centerAuto = "Center";
	private SendableChooser<String> chooser = new SendableChooser<>();
	private String autoSelected;
	String gameData;
	String position;

	ArrayList<Double> movementLinear;
	ArrayList<Double> movementRotate;
	boolean recording;
	boolean playing;
	int play_counter;

	Joystick manip;
	Joystick samIsUseless;
	UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);;

	Talon leftArm;
	Talon rightArm;
	Talon elevator1;
	Talon elevator2;
	Talon leftFront;
	Talon leftBack;
	Talon rightFront;
	Talon rightBack;
	DifferentialDrive chassis;
	SpeedControllerGroup leftAmogh;
	SpeedControllerGroup rightAmogh;
	public static int LF;
	public static int LB;
	public static int RB;
	public static int RF;
	public static int E1 = 1;
	public static int E2 = 2;
	public static int LARM = 3;
	public static int RARM = 4;

	/*Compressor compressor;
	DoubleSolenoid leftRamp;
	DoubleSolenoid rightRamp;
	PressureSensor psense;
	public static int LRAMP = 0;
	public static int RRAMP = 1;
	public static int COMPRESSOR_CAN = 0;
	public static int A_PSENSE = 0;*/

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Adds choices to SmartDashboard
		chooser.addDefault("Center", centerAuto);
		chooser.addObject("Right", rightAuto);
		chooser.addObject("Left", leftAuto);
		chooser.addObject("Teleop Recorded", recordingAuto);

		//Joysticks and other controllers
		manip = new Joystick(0);
		samIsUseless = new Joystick(1);

		//Motor controllers
		//leftArm = new Talon(LARM);
		//rightArm = new Talon(RARM);
		//elevator1 = new Talon(E1);
		//elevator2 = new Talon(E2);
		leftFront = new Talon(LF);
		leftBack = new Talon(LB);
		rightFront = new Talon(RF);
		rightBack = new Talon(RB);
		leftAmogh = new SpeedControllerGroup(leftFront, leftBack);
		rightAmogh = new SpeedControllerGroup(rightFront, rightBack);
		chassis = new DifferentialDrive(leftAmogh, rightAmogh);

		//Pneumatics
		/*compressor = new Compressor(0);
		leftRampAmogh = new DoubleSolenoid(6, 7);
		rightRampAmogh = new DoubleSolenoid(4, 5);
		psense = new PressureSensor(A_PSENSE);*/

		//Resets counting variables and default booleans
		recording = false;
		playing = false;
		
		movementLinear = new ArrayList<Double>();
		movementRotate = new ArrayList<Double>();
	}

	/**
	 * Run once at the beginning of auto
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		position = Character.toString(gameData.charAt(0));
		playing = false;
		play_counter = 0;

		//Left auto
		if(autoSelected == leftAuto) {
			if(position == "L"){
				//if switch is left
			} else {
				//if switch is right
			}
			//Right auto
		} else if(autoSelected == rightAuto) {
			if(position == "L"){
				//if switch is left
			} else {
				//if switch is right
			}
			//Center auto
		} else if(autoSelected == centerAuto) {
			if(position == "L"){
				//if switch is left
			} else {
				//if switch is right
			}
			//Error catcher
		} else if(autoSelected == recordingAuto){
			playing = true;
		} else {
			System.out.println("Failed to choose auto position");
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putBoolean("Playing back: ", playing);
		if(playing){
			if(play_counter <= movementLinear.size() - 1) {
				chassis.arcadeDrive(movementLinear.get(play_counter), movementRotate.get(play_counter));
				play_counter++;
			} else {
				playing = false;
				play_counter = 0;
			}
		}
	}
	
	/**
	 * This function is called once at the beginning of teleop mode.
	 */
	@Override
	public void teleopInit() {
		recording = false;
		playing = false;
		movementLinear.clear();
		movementRotate.clear();
		play_counter = 0;
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		//RECORDING
		if(manip.getRawButton(7)){
			recording = true;
		}
		//STOP RECORDING
		if(manip.getRawButton(9)){
			recording = false;
		}
		//PLAYBACK
		if(manip.getRawButton(1)){
			playing = true;
		}
		//RESET ARRAYS
		if(manip.getRawButton(8)){
			movementLinear.clear();
			movementRotate.clear();
		}
		//SAVING
		if(manip.getRawButtonPressed(11)){
			recording = false;
			try {
				FileOutputStream linfile = new FileOutputStream("/home/lvuser/lin" + NEW_RECORDING_NAME +".ser");
				FileOutputStream rotfile = new FileOutputStream("/home/lvuser/rot" + NEW_RECORDING_NAME + ".ser");
				ObjectOutputStream linout = new ObjectOutputStream(linfile);
				ObjectOutputStream rotout = new ObjectOutputStream(rotfile);
				linout.writeObject(movementLinear);
				rotout.writeObject(movementRotate);
				rotout.close();
				linout.close();
				linfile.close();
				rotfile.close();
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
		//LOADING
		if(manip.getRawButtonPressed(12)){
			recording = false;
			movementLinear.clear();
			movementRotate.clear();
			try {
				FileInputStream readfilelin = new FileInputStream("/home/lvuser/lin" + SAVED_NAME + ".ser");
				FileInputStream readfilerot = new FileInputStream("/home/lvuser/rot" + SAVED_NAME + ".ser");
				ObjectInputStream in = new ObjectInputStream(readfilelin);
				movementLinear = (ArrayList<Double>) in.readObject();
				in = new ObjectInputStream(readfilerot);
				movementRotate = (ArrayList<Double>) in.readObject();
			} catch (Exception i){
				i.printStackTrace();
			}
		}
		SmartDashboard.putBoolean("Recording?", recording);

		if(recording){
			movementLinear.add(manip.getRawAxis(1));
			movementRotate.add(manip.getRawAxis(0));
		}

		if(playing){
			if(play_counter <= movementLinear.size() - 1) {
				chassis.arcadeDrive(movementLinear.get(play_counter), movementRotate.get(play_counter));
				play_counter++;
			} else {
				playing = false;
				play_counter = 0;
			}
		}
	}
	
	/**
	 * This function is called once at the beginning of test mode.
	 */
	@Override
	public void testInit(){
		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic(){

	}
}
