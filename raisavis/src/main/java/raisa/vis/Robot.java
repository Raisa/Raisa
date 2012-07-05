package raisa.vis;

import java.awt.geom.Point2D.Float;

public class Robot {
	public float heading;
	public Float position;

	public Robot() {
		this(0.0f, new Float());		
	}
	
	public Robot(float heading, Float robot) {
		this.heading = heading;
		this.position = robot;
	}
}