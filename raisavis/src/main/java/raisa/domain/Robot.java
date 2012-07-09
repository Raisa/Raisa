package raisa.domain;

import java.awt.geom.Point2D.Float;

public class Robot {
	public final static float ROBOT_WIDTH = 10f;
	public final static float ROBOT_LENGTH = 20f;
	public final static float WHEEL_DIAMETER = 3.6f;
	public final static float TICK_RADIANS = (float) Math.PI / 8.0f;
	
	public float heading;
	public Float positionLeftWheel;
	public Float positionRightWheel;

	public Robot() {
		this((float)Math.PI, new Float());		
	}
	
	public Robot(float heading, Float position) {
		this.heading = heading;
		float tmpX = position.x - ROBOT_WIDTH/2.0f;
		float tmpY = position.y;
		this.positionLeftWheel = new Float(
				(float)Math.cos(heading) * tmpX - (float)Math.sin(heading) * tmpY,
				(float)Math.sin(heading) * tmpX + (float)Math.cos(heading) * tmpY);
		tmpX = position.x + ROBOT_WIDTH/2.0f;
		this.positionRightWheel = new Float(
				(float)Math.cos(heading) * tmpX - (float)Math.sin(heading) * tmpY,
				(float)Math.sin(heading) * tmpX + (float)Math.cos(heading) * tmpY);
	}
	
	public Float getPosition() {
		return new Float(
				(positionLeftWheel.x + positionRightWheel.x) / 2.0f,
				(positionLeftWheel.y + positionRightWheel.y) / 2.0f);
	}
	
	public float getHeading() {
		return heading;
	}

	public void setHeading(float heading) {
		this.heading = heading;
	}

	public Robot moveRobot(Sample sample) {
		Robot r = new Robot();		
		float leftWheelTrip = (WHEEL_DIAMETER * sample.getLeftTrackTicks() * TICK_RADIANS) / 2.0f;
		float rightWheelTrip = (WHEEL_DIAMETER * sample.getRightTrackTicks() * TICK_RADIANS) / 2.0f;
		
		float h = sample.getCompassDirection();
		r.positionLeftWheel = new Float(
				this.positionLeftWheel.x + leftWheelTrip * (float)Math.sin(h),
				this.positionLeftWheel.y - leftWheelTrip * (float)Math.cos(h));
				
		r.positionRightWheel = new Float(
				this.positionRightWheel.x + rightWheelTrip * (float)Math.sin(h),
				this.positionRightWheel.y - rightWheelTrip * (float)Math.cos(h));
		r.heading = h;
		return r;
	}
	
	public String toString() {
		return 
			"position (x=" + getPosition().x + ",y=" + getPosition().y + "), " +
			"leftwheel (x=" + positionLeftWheel.x + ",y=" + positionLeftWheel.y + "), " +
			"rightwheel (x=" + positionRightWheel.x + ",y=" + positionRightWheel.y + ")";
	}
	
	
}