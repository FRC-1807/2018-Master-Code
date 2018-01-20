package org.usfirst.frc.team1807.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class PressureSensor extends AnalogInput {
	
	private double psi;
	
	public PressureSensor(int analog_port){
		super(analog_port);
	}
	
	public double getPSI(){
		psi = 250.0 * this.getVoltage() / 5.0 - 25.0;
		return psi;
	}
}
