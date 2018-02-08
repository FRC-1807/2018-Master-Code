package org.usfirst.frc.team1807.robot;

import edu.wpi.first.wpilibj.Encoder;

public class RedbirdEncoder extends Encoder {

	public RedbirdEncoder(int channelA, int channelB) {
		super(channelA, channelB);
	}
	
	public double getFrontBumper(){
		return getDistance() + 39.5;
	}
}
