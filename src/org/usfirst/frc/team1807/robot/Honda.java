package org.usfirst.frc.team1807.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Ultrasonic.Unit;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Honda extends DifferentialDrive{
	
	private Ultrasonic front;
	private boolean reversed;
	
	public Honda(SpeedController leftFrontMotor, SpeedController leftBackMotor, SpeedController rightFrontMotor, SpeedController rightBackMotor) {
		super(new SpeedControllerGroup(leftFrontMotor, leftBackMotor), new SpeedControllerGroup(rightFrontMotor, rightBackMotor));
	}
	
	public void setUltra(Ultrasonic frontOfBotUltra){
		front = frontOfBotUltra;
	}
	
	public void setUltra(int channelPing, int channelEcho){
		front = new Ultrasonic(channelPing, channelEcho);
	}
	
	public double getUltra(){
		return front.getRangeInches();
	}
	
	public void odyssey(Joystick joy){
		if(reversed){
			arcadeDrive(-joy.getRawAxis(1), joy.getRawAxis(0));
		} else {
			arcadeDrive(joy.getRawAxis(1), joy.getRawAxis(0));
		}
	}
	
	public void odyssey(double linearSpeed, double rotationalSpeed){
		if(reversed){
			arcadeDrive(-linearSpeed, rotationalSpeed);
		} else {
			arcadeDrive(linearSpeed, rotationalSpeed);
		}
	}
	
	public void setReversed(boolean x){
		reversed = x;
	}
}