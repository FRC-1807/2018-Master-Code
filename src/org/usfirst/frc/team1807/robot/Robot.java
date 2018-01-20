/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1807.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main, what will be finalized code for the 2018 FIRST Power Up Game.
 * @author - Joey & Peter
 * @since - we been stunting on niggas
 */
public class Robot extends IterativeRobot {
	
	Talon frontRightDrive = new Talon(0);
	Talon backRightDrive = new Talon(1);
	Talon frontLeftDrive = new Talon(9);
	Talon backLeftDrive = new Talon(8);
	RobotDrive myRDrive = new RobotDrive(frontRightDrive, backRightDrive, frontLeftDrive, backLeftDrive);
	
	private static final String RECORDING_NAME = "shit";
	private static final String SAVED_NAME = "shit";
	
	private static final String leftAuto = "Left";
	private static final String rightAuto = "Right";
	private static final String centerAuto = "Center";
	private SendableChooser<String> chooser = new SendableChooser<>();
	private String autoSelected;
	String gameData;
	String position;
	
	ArrayList<Double> movementLinear = new ArrayList<Double>();
	ArrayList<Double> movementRotate = new ArrayList<Double>();
	boolean recording;
	boolean saving;
	boolean loading;
	int load_counter;
	int save_counter;
	
	Joystick manip;
	Joystick samIsUseless;
	UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);;
	
	Talon leftArm;
	Talon rightArm;
	Talon elevator1;
	Talon elevator2;
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
		
		//Joysticks and other controllers
		manip = new Joystick(0);
		samIsUseless = new Joystick(1);
		
		//Motor controllers
		//leftArm = new Talon(LARM);
		//rightArm = new Talon(RARM);
		//elevator1 = new Talon(E1);
		//elevator2 = new Talon(E2);
		
		//Pneumatics
		/*compressor = new Compressor(0);
		leftRamp = new DoubleSolenoid(6, 7);
		rightRamp = new DoubleSolenoid(4, 5);
		psense = new PressureSensor(A_PSENSE);*/
		
		//Resets counting variables and default booleans
		recording = false;
		saving = false;
		save_counter = 0;
		load_counter = 0;
		loading = false;
		
		movementRotate.clear();
		movementLinear.clear();
		myRDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		myRDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
		myRDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		myRDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
	}

	/**
	 * Run once at the beginning of auto
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		position = Character.toString(gameData.charAt(0));
		
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
		} else {
			System.out.println("Failed to choose auto position");
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//eat some balls
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		/*SmartDashboard.putNumber("PSI", psense.getPSI());
		SmartDashboard.putBoolean("Compressor Enabled: ", compressor.enabled());
		SmartDashboard.putBoolean("Pressure Switch: ", compressor.getPressureSwitchValue());*/
		
		
	}
	
	@Override
	public void testInit(){
		recording = false;
		saving = false;
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void testPeriodic(){
		myRDrive.arcadeDrive(manip);
		
		if(manip.getRawButton(7)){
			recording = true;
		}
		if(manip.getRawButton(9)){
			recording = false;
		}
		if(manip.getRawButton(11)){
			saving = true;
		}
		if(manip.getRawButton(12)){
			loading = true;
		}
		
		SmartDashboard.putBoolean("Loading...", loading);
		SmartDashboard.putBoolean("Saving...", saving);
		SmartDashboard.putBoolean("Recording?", recording);
		
		if(recording){
			movementLinear.add(manip.getRawAxis(1));
			movementRotate.add(manip.getRawAxis(0));
		}
		if(saving){
			FileWriter wr = null;
			try {
				wr = new FileWriter(new File("/home/lvuser/" + RECORDING_NAME + ".txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(save_counter <= movementLinear.size() - 1){
				try {
					wr.write(movementLinear.get(save_counter).toString() + ":" + movementRotate.get(save_counter).toString() + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				saving = false;
			}
		}
		/*if(loading){
			if(){
			
			} else {
				loading = false;
			}
			load_counter++;
		}*/
	}
}
