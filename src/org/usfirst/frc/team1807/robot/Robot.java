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
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
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
	DifferentialDrive honda;
	Talon collection;
	Talon hinge;


	Compressor compressor;
	Solenoid collector;
	PressureSensor psense;

	UsbCamera camera;
	Encoder rightEnc;
	Encoder leftEnc;
	ADXRS450_Gyro gyro;
	//Ultrasonic block_detector;
	Potentiometer pot;

	DoubleSolenoid leftRamp = new DoubleSolenoid(0, 1);
	DoubleSolenoid rightRamp = new DoubleSolenoid(6, 7);

	DoubleSolenoid ramps = new DoubleSolenoid (2,3);

	Joystick sam = new Joystick(1);

	boolean leftrampup = false;
	boolean rightrampup = false;
	
	boolean reversedDrive = false;

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

		reversedDrive=false;

		//Motor controllers
		elevator = new Talon(9);
		leftFront = new Talon(LF);
		leftBack = new Talon(LB);
		rightFront = new Talon(RF);
		rightBack = new Talon(RB);
		SpeedControllerGroup leftDrive = new SpeedControllerGroup(leftFront, leftBack);
		SpeedControllerGroup rightDrive = new SpeedControllerGroup(rightFront, rightBack);
		honda = new DifferentialDrive(leftDrive, rightDrive);
		collection = new Talon(6);


		//Pneumatics and sensors
		compressor = new Compressor(COMPRESSOR_CAN);

		collector = new Solenoid(4);
		psense = new PressureSensor(1);

		//leftEnc = new Encoder(LEFTSOURCEA, LEFTSOURCEB, true, CounterBase.EncodingType.k4X);
		//rightEnc = new Encoder(RIGHTSOURCEA, RIGHTSOURCEB, true, CounterBase.EncodingType.k4X);
		//camera = CameraServer.getInstance().startAutomaticCapture(0);
		//gyro = new ADXRS450_Gyro();
		//block_detector = new Ultrasonic(D_ULTRA1_PING, D_ULTRA1_ECHO);
		//honda.setUltra(D_ULTRA2_PING, D_ULTRA2_ECHO);
		pot = new AnalogPotentiometer(0);

		//Resets counting variables and default booleans
		recording = false;
		playing = false;
		elevator_moving = 0;

		movementLinear = new ArrayList<Double>();
		movementRotate = new ArrayList<Double>();

		honda.setMaxOutput(.6);
		compressor.stop();

		leftrampup=false;
		rightrampup=false;

	}

	/**
	 * Run once at the beginning of auto
	 */
	@Override
	public void autonomousInit() {
		compressor.stop();
		/*auto = "amogh";
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
		}*/

		//driveTo(1950, 0.4);

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	
	
	
	
	@Override
	public void autonomousPeriodic() {

		compressor.stop();
		honda.arcadeDrive(.6, 0);
		//SmartDashboard.putNumber(
		/*"Ultrasonic range: ", honda.getUltra());
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
		 */
	}

	/**
	 * This function is called once at the beginning of teleop mode.
	 */
	@Override
	public void teleopInit() {
		compressor.stop();
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

		/*
		if(manip.getRawButton(4)) {
			leftEnc.reset();
			rightEnc.reset();
		}
		 */
		//SmartDashboard.putNumber("lenc: ", leftEnc.getDistance());
		//SmartDashboard.putNumber("renc: ", rightEnc.getDistance());


		SmartDashboard.putNumber("PSI: ", psense.getPSI());



		if(sam.getRawButton(1) || manip.getRawButton(1)) {
			collection.set(-.4);
			collector.set(true);
		} else if(sam.getRawButton(5) || manip.getRawButton(6)) {
			collection.set(.5);
		} else if (sam.getRawButton(3) || manip.getRawButton(4)) {
			collection.set(-.5);
		} else {
			collector.set(false);
			if(Math.abs(manip.getRawAxis(0)) > -.2 || Math.abs(manip.getRawAxis(0)) < .2){
				collection.set(-Math.abs(manip.getRawAxis(0)));
			} else {
				collection.set(-.2);
			}
			collection.set(-Math.abs(manip.getRawAxis(0))/3);
		}
		
		if((manip.getRawButton(5) || sam.getRawButton(6)) && pot.get() > .36) {
			elevator.set(-.95);
		} else if ((manip.getRawButton(3) || sam.getRawButton(4)) && pot.get() < .955) { 
			elevator.set(.85);
		} else {
			elevator.set(-.154);
		}





		if(sam.getRawButton(10)) {
			leftrampup=true;
			leftRamp.set(DoubleSolenoid.Value.kForward);
		} else if (!leftrampup) {
			leftRamp.set(DoubleSolenoid.Value.kReverse);
		}

		if(sam.getRawButton(12)) {
			leftrampup=false;
		}
		if(sam.getRawButton(11)) {
			rightrampup=false;
		}

		if(sam.getRawButton(9) ) {
			rightRamp.set(DoubleSolenoid.Value.kForward);
			rightrampup=true;
		} else if (!rightrampup){
			rightRamp.set(DoubleSolenoid.Value.kReverse);
		}



		if(sam.getRawButton(7)) {
			ramps.set(DoubleSolenoid.Value.kForward);
		} else {
			ramps.set(DoubleSolenoid.Value.kReverse);
		}
		
		
		
		
		
		if(manip.getRawAxis(3)<0 || sam.getRawAxis(3)<0) {
			compressor.start();
		} else {
			compressor.stop();
		}

		

		
		if(manip.getRawButtonPressed(9) && manip.getRawAxis(1) <.2 && manip.getRawAxis(1) > -.2) {
			reversedDrive =false;
		} else if (manip.getRawButtonPressed(11)) {
			reversedDrive = true;
		}
		

		if (manip.getRawButton(8)){
			collection.set(manip.getRawAxis(1));
		} else {
			if(!reversedDrive) {
				honda.arcadeDrive(manip.getRawAxis(1), manip.getRawAxis(0));
				
			} else {
				honda.arcadeDrive(-manip.getRawAxis(1), manip.getRawAxis(0));
			}
			
		}

		//Update SmartDashboard values
		SmartDashboard.putBoolean("Recording: ", recording);
		SmartDashboard.putBoolean("Saving: ", saving);
		SmartDashboard.putNumber("Potty: ", pot.get());
		//SmartDashboard.putNumber("Left: ", leftEnc.get());
		//SmartDashboard.putNumber("Right: ", rightEnc.get());
		//SmartDashboard.putNumber("Gyroscope: ", gyro.getAngle());



		/*
		if(manip.getRawButtonPressed(5)) {
			elevator_moving = 1;
		} else if (manip.getRawButtonPressed(3)) {
			elevator_moving = -1;
		}

		if(elevator_moving == 1) {
			if(elevatorTo(.23)) {
				elevator_moving=0;
			}
			elevatorTo(.23);
		} else if (elevator_moving == 0) {
			elevator.set(0);
		} else if (elevator_moving == -1) {
			if(elevatorTo(.88)) {
				elevator_moving=0;
			}
			elevatorTo(.88);
		}
		 */


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
		double wiggle = 10;
		leftEnc.reset();
		rightEnc.reset();
		double average = (leftEnc.getDistance() + rightEnc.getDistance())/2;
		while(average < distanceInches-wiggle || average > distanceInches+wiggle){
			average = (leftEnc.getDistance() + rightEnc.getDistance())/2;
			if(average < distanceInches-wiggle){
				honda.arcadeDrive(-speed, 0);
			} else {
				honda.arcadeDrive(speed, 0);
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