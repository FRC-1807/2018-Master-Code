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
 */
public class Robot extends IterativeRobot implements Constants{

	private SendableChooser<String> chooser;
	private String autoSelected;
	private String gameData;

	ArrayList<Double> movementLinear = new ArrayList<Double>();
	ArrayList<Double> movementRotate = new ArrayList<Double>();
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
	boolean poopstart = false;
	int pooptimer = 0;
	boolean leftScale = false;
	boolean rightScale = false;
	boolean leftFarSwitch = false;
	boolean leftCloseSwitch = false;
	boolean rightFarSwitch = false;
	boolean rightCloseSwitch = false;
	boolean doSwitch = false;
	boolean doAuto = true;
	char ourSide;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {

		//Adds choices to SmartDashboard
		chooser = new SendableChooser<>();
		chooser.addDefault("Far Left/Right", centerAuto);
		chooser.addObject("Right", rightAuto);
		chooser.addObject("Left", leftAuto);
		chooser.addObject("Dont fucking move", DONTMOVE);

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

		honda.setSafetyEnabled(false);

	}

	/**
	 * Run once at the beginning of auto
	 */
	@Override
	public void autonomousInit() {
		
		movementLinear.clear();
		movementRotate.clear();
		try {
			FileInputStream readfilelin = new FileInputStream("/home/lvuser/linrr.ser");
			FileInputStream readfilerot = new FileInputStream("/home/lvuser/rotrr.ser");
			ObjectInputStream in = new ObjectInputStream(readfilelin);
			movementLinear = (ArrayList<Double>) in.readObject();
			in = new ObjectInputStream(readfilerot);
			movementRotate = (ArrayList<Double>) in.readObject();
		} catch (Exception i){
			i.printStackTrace();
		}

		compressor.stop();
		pooptimer=0;

		recording = false;
		//playing = false;
		saving = false;
		poopstart=false;
		//elevatorAuto();

		playing=true;
		collection.set(0);

		autoSelected = chooser.getSelected();
		gameData = DriverStation.getInstance().getGameSpecificMessage().toUpperCase();
		playing = false;
		play_counter = 0;
		doAuto = true;

		try {
			FileInputStream readfilelin = new FileInputStream("/home/lvuser/linrr.ser");
			FileInputStream readfilerot = new FileInputStream("/home/lvuser/rotrr.ser");
			ObjectInputStream in = new ObjectInputStream(readfilelin);
			movementLinear = (ArrayList<Double>) in.readObject();
			in = new ObjectInputStream(readfilerot);
			movementRotate = (ArrayList<Double>) in.readObject();
		} catch (Exception i){
			i.printStackTrace();
		}
		
		if(autoSelected == leftAuto){
			if(gameData.charAt(0) == 'L'){
				doSwitch = true;
			}
		} else if(autoSelected == rightAuto){
			if(gameData.charAt(0) == 'R'){
				doSwitch = true;
			}
		} else if(autoSelected == centerAuto){
			doSwitch = false;
		} else {
			doAuto = false;
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */




	@Override
	public void autonomousPeriodic() {
		
		SmartDashboard.putBoolean("leftScale", leftScale);
		SmartDashboard.putBoolean("rightScale", rightScale);
		SmartDashboard.putBoolean("leftFarSwitch", leftFarSwitch);
		SmartDashboard.putBoolean("rightFarSwitch", rightFarSwitch);
		SmartDashboard.putBoolean("leftCloseSwitch", leftCloseSwitch);
		SmartDashboard.putBoolean("rightCloseSwitch", rightCloseSwitch);
		
		SmartDashboard.putNumber("Potentiometer: ", pot.get());

		if(playing){
			if(play_counter <= movementLinear.size() - 1) {
				honda.arcadeDrive(movementLinear.get(play_counter), 0);
				play_counter++;
			} else {
				playing = false;
				play_counter = 0;
			}
		} else {

			if(!poopstart) {
				elevatorTo(.36);
				if(elevatorTo(.36)) {
					poopstart = true;
					pooptimer=40;
				}
			}
		}


		if(pooptimer > 0) {
			if(pooptimer==1) {
				pooptimer=-3;
			}
			pooptimer--;
			leftFront.set(-.3);
			leftBack.set(-.3);
			rightFront.set(.3);
			rightBack.set(.3);

		} else if (pooptimer<0 && pooptimer >-30 && doSwitch){
			pooptimer--;
			collection.set(.25);
			leftFront.set(0);
			leftBack.set(0);
			rightFront.set(0);
			rightBack.set(0);
		} else if (pooptimer <= -20){
			collection.set(0);
		}


		//compressor.stop();
		//honda.arcadeDrive(.6, 0);
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
	public void testPeriodic() {
		if(manip.getRawButtonPressed(2)) {
			elevatorAuto();
		}
	}


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
			if(Math.abs(manip.getRawAxis(0)) > -.11 || Math.abs(manip.getRawAxis(0)) < .11){
				collection.set(-Math.abs(manip.getRawAxis(0)));
			} else {
				collection.set(-.2);
			}
			collection.set(-Math.abs(manip.getRawAxis(0))/3);
		}



		if((manip.getRawButton(5) || sam.getRawButton(6)) && pot.get() > .36) {
			elevator.set(-.95);
		} else if ((manip.getRawButton(3) || sam.getRawButton(4)) && pot.get() < .969) { 
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



		/*
		if(manip.getRawButtonPressed(9) && manip.getRawAxis(1) <.2 && manip.getRawAxis(1) > -.2) {
			reversedDrive =false;
		} else if (manip.getRawButtonPressed(11)) {
			reversedDrive = true;
		}
		 */

		if (manip.getRawButton(8)){
			collection.set(manip.getRawAxis(1));
		} else {
			if(!reversedDrive) {
				honda.arcadeDrive(manip.getRawAxis(1), manip.getRawAxis(0)); //manip.getRawAxis(0)
			} else {
				honda.arcadeDrive(-manip.getRawAxis(1), manip.getRawAxis(0)); //manip.getRawAxis(0)
			}

		}


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


		//RECORDING
		if(manip.getRawButtonPressed(7)){
			recording = true;
		}
		//STOP RECORDING
		if(manip.getRawButtonPressed(9)){
			recording = false;
		}
		//PLAYBACK
		if(manip.getRawButtonPressed(8)){
			playing = false;
		}
		 
		//RESET ARRAYS
		if(manip.getRawButtonPressed(10)){
			movementLinear.clear();
			movementRotate.clear();
		}
		//SAVING
		if(manip.getRawButtonPressed(12)){
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
		if(manip.getRawButtonPressed(11)){
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
		}*/

	}

	public boolean elevatorTo(double degrees){

		if (pot.get() > degrees) {
			elevator.set(-1);
		} else {
			elevator.set(-.154);
			return true;
		}
		return false;
	}

	public void elevatorAuto() {
		elevatorTo(.38); //.36
		pooptimer = 40;

	}

}