/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1807.robot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main, what will be finalized code for the 2018 FIRST Power Up Game.
 * @author - Joey, Peter and Friends
 * @since - we been stunting on niggas
 */
public class Robot extends IterativeRobot implements Constants{

	private SendableChooser<String> chooser;
	SendableChooser<String> lineChooser;
	private String autoSelected;
	private String gameData;
	char position;
	String auto;

	ArrayList<Double> movementLinear;
	ArrayList<Double> movementRotate;
	boolean recording;
	boolean playing;
	boolean saving;
	int elevator_moving;
	int play_counter;
	boolean moveElevator;
	boolean crossLine;

	Joystick manip;
	Joystick samIsUseless;

	Talon elevator;
	Talon leftFront;
	Talon leftBack;
	Talon rightFront;
	Talon rightBack;
	Honda honda;
	Talon collection;
	Talon hinge;


	Compressor compressor;
	DoubleSolenoid leftAmogh;
	DoubleSolenoid rightAmogh;
	Solenoid collector;
	PressureSensor psense;

	UsbCamera camera;
	RedbirdEncoder rightEnc;
	RedbirdEncoder leftEnc;
	ADXRS450_Gyro gyro;
	//Ultrasonic block_detector;
	Potentiometer pot;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Adds choices to SmartDashboard
		chooser = new SendableChooser<>();
		chooser.addDefault("Center", centerAuto);
		chooser.addObject("Right", rightAuto);
		chooser.addObject("Left", leftAuto);
		chooser.addObject("Dont fucking move", DONTMOVE);
		lineChooser = new SendableChooser<>();
		lineChooser.addDefault("Cross Line", doLine);
		lineChooser.addObject("No Line", noLine);

		//Joysticks and other controllers
		manip = new Joystick(manipPort);
		samIsUseless = new Joystick(joyPort);

		//Motor controllers
		elevator = new Talon(ELEVATOR);
		leftFront = new Talon(LF);
		leftBack = new Talon(LB);
		rightFront = new Talon(RF);
		rightBack = new Talon(RB);
		honda = new Honda(leftFront, leftBack, rightFront, rightBack);
		collection = new Talon(6);
		hinge = new Talon(4);

		//Pneumatics and sensors
		compressor = new Compressor(COMPRESSOR_CAN);
		leftAmogh = new DoubleSolenoid(6, 7);
		rightAmogh = new DoubleSolenoid(4, 5);
		collector = new Solenoid(2);
		psense = new PressureSensor(A_PSENSE);

		leftEnc = new RedbirdEncoder(LEFTSOURCEA, LEFTSOURCEB);
		rightEnc = new RedbirdEncoder(RIGHTSOURCEA, RIGHTSOURCEB);
		camera = CameraServer.getInstance().startAutomaticCapture(0);
		gyro = new ADXRS450_Gyro();
		//block_detector = new Ultrasonic(D_ULTRA1_PING, D_ULTRA1_ECHO);
		//honda.setUltra(D_ULTRA2_PING, D_ULTRA2_ECHO);
		pot = new AnalogPotentiometer(A_POT);

		//Resets counting variables and default booleans
		recording = false;
		playing = false;
		elevator_moving = 0;

		movementLinear = new ArrayList<Double>();
		movementRotate = new ArrayList<Double>();
	}

	/**
	 * Run once at the beginning of auto
	 */
	@Override
	public void autonomousInit() {
		auto = "amogh";
		autoSelected = chooser.getSelected();
		String lineSelected = lineChooser.getSelected(); 
		gameData = DriverStation.getInstance().getGameSpecificMessage().toUpperCase();
		position = gameData.charAt(0);
		playing = false;
		play_counter = 0;

		if(lineSelected == doLine) {
			crossLine = true;
			playing = false;
		} else {
			crossLine = false;
			playing = true;
		}

		if(autoSelected == DONTMOVE) {
			playing = false;
		} else if(crossLine) {
			playing = true;
			auto = "cross_line";
		}
		//Left auto
		else if(autoSelected == leftAuto) {
			if(position == 'L'){
				auto = "left_switch_left";
			} else {
				auto = "left_switch_right";
			}
			//Right auto
		} else if(autoSelected == rightAuto) {
			if(position == 'L'){
				auto = "right_switch_left";
			} else {
				auto = "right_switch_right";
			}
			//Center auto
		} else if(autoSelected == centerAuto) {
			if(position == 'L'){
				auto = "center_switch_left";
			} else {
				auto = "center_switch_right";
			}
			//Error catcher
		} else {
			System.out.println("Failed to choose auto position");
			playing = false;
		}

		try {
			FileInputStream readfilelin = new FileInputStream("/home/lvuser/lin" + auto + ".ser");
			FileInputStream readfilerot = new FileInputStream("/home/lvuser/rot" + auto + ".ser");
			ObjectInputStream in = new ObjectInputStream(readfilelin);
			movementLinear = (ArrayList<Double>) in.readObject();
			in = new ObjectInputStream(readfilerot);
			movementRotate = (ArrayList<Double>) in.readObject();
		} catch (Exception i){
			i.printStackTrace();
		}

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Ultrasonic range: ", honda.getUltra());
		//For playing recorded auto
		SmartDashboard.putBoolean("Playing back: ", playing);

		if(playing){
			if(play_counter <= movementLinear.size() - 1) {
				honda.arcadeDrive(movementLinear.get(play_counter), movementRotate.get(play_counter));
				play_counter++;
			} else {
				playing = false;
				play_counter = 0;
				if(moveElevator) {
					elevatorAuto();
				}
			}
		} else {
			honda.stopMotor();
		}
	}

	/**
	 * This function is called once at the beginning of teleop mode.
	 */
	@Override
	public void teleopInit() {
		recording = false;
		playing = false;
		saving = false;
		movementLinear.clear();
		movementRotate.clear();
		play_counter = 0;
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		//Update SmartDashboard values
		SmartDashboard.putBoolean("Recording: ", recording);
		SmartDashboard.putBoolean("Saving: ", saving);
		SmartDashboard.putBoolean("Playing: ", playing);
		SmartDashboard.putNumber("Ultrasonic range: ", honda.getUltra());
		SmartDashboard.putNumber("Left: ", leftEnc.get());
		SmartDashboard.putNumber("Right: ", rightEnc.get());
		SmartDashboard.putNumber("Gyroscope: ", gyro.getAngle());

		if(manip.getRawButtonPressed(5)) {
			elevator_moving = 1;
		} else if (manip.getRawButtonPressed(3)) {
			elevator_moving = -1;
		}

		if(elevator_moving == 1) {
			if(elevatorTo(top)) {
				elevator_moving=0;
			}
			elevatorTo(top);
		} else if (elevator_moving == 0) {
			elevator.set(0);
		} else if (elevator_moving == -1) {
			if(elevatorTo(bottom)) {
				elevator_moving=0;
			}
			elevatorTo(bottom);
		}



		//RECORDING
		if(manip.getRawButtonPressed(7)){
			recording = true;
		}
		//STOP RECORDING
		if(manip.getRawButtonPressed(9)){
			recording = false;
		}
		//PLAYBACK
		if(manip.getRawButtonPressed(1)){
			playing = true;
		}
		//RESET ARRAYS
		if(manip.getRawButtonPressed(8)){
			movementLinear.clear();
			movementRotate.clear();
		}
		//SAVING
		if(manip.getRawButtonPressed(11)){
			recording = false;
			saving = true;
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
		saving = false;
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

		if(recording){
			movementLinear.add(manip.getRawAxis(1));
			movementRotate.add(manip.getRawAxis(0));
		}

		//For playing recorded auto
		SmartDashboard.putBoolean("Playing back: ", playing);
		if(playing){
			if(play_counter <= movementLinear.size() - 1) {
				honda.arcadeDrive(movementLinear.get(play_counter), movementRotate.get(play_counter));
				play_counter++;
			} else {
				playing = false;
				play_counter = 0;
			}
		}


	}

	public boolean elevatorTo(double degrees){
		double wiggle = 0.25;
		if(pot.get() > degrees+wiggle){
			//elevator.set(-.5);
		} else if (pot.get() < degrees-wiggle){
			//elevator.set(.5);
		} else {
			return true;
		}
		return false;
	}

	public boolean driveTo(double distanceInches, double speed){
		double wiggle = 0.25;
		leftEnc.reset();
		rightEnc.reset();
		double average = (leftEnc.getFrontBumper() + rightEnc.getFrontBumper())/2;
		while(average < distanceInches-wiggle || average > distanceInches+wiggle){
			average = (leftEnc.getFrontBumper() + rightEnc.getFrontBumper())/2;
			if(average < distanceInches-wiggle){
				honda.odyssey(speed, 0);
			} else {
				honda.odyssey(-.25, 0);
			}
		}
		honda.stopMotor();
		return true;
	}

	public void elevatorAuto() {
		int hingeTimer = 30;
		while(!elevatorTo(top)) {
			hingeTimer--;
			if(hingeTimer > 0) {
				hinge.set(-1);
			} else {
				hinge.set(0);
			}
		}
		int shootingTimer = 60;
		while(shootingTimer > 0) {
			shootingTimer--;
			collection.set(.4);
		}
		collection.set(0);
	}

	/*
	public boolean turnTo(double degrees){
		double wiggle = 3;
		double angle = 0;
		if(degrees < 0){
			honda.setReversed(true);
		}
		while(angle < degrees-wiggle || angle > degrees+wiggle){
			angle = gyro.getAngle();
			if(angle > degrees+wiggle) {
				honda.odyssey(0, -0.25);
			} else if (angle < degrees-wiggle){
				honda.odyssey(0, 0.25);
			}
		}
		honda.stopMotor();
		honda.setReversed(false);
		return true;
	}
	 */
}