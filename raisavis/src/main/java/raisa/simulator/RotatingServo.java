package raisa.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotatingServo {
	private static final Logger log = LoggerFactory.getLogger(RotatingServo.class);
	private float heading;
	private float rotatingSpeed = 60.0f / 0.17f;
	private float minHeading = -90;
	private float maxHeading = 90;
	private static final int LEFT = -1;
	private static final int RIGHT = +1;
	private float scanOffset = 0;
	private float scanStep = 20;
	private float nextScanHeading = 10;
	private int direction = RIGHT;
	
	public RotatingServo() {
		this.heading = (minHeading + maxHeading) / 2.0f;
	}

	public RotatingServo rotate(float timestep) {
		heading += direction * timestep * rotatingSpeed;
		int nextDirection = direction;
		if (direction == LEFT && heading < nextScanHeading) {
			heading = nextScanHeading;
			scan();
			nextScanHeading -= scanStep;
		}
		if (direction == RIGHT && heading > nextScanHeading) {
			heading = nextScanHeading;
			scan();
			nextScanHeading += scanStep;
		}
		if (heading >= maxHeading) {
			heading = maxHeading;
			scan();
			nextDirection = LEFT;
			nextScanHeading = maxHeading - scanStep - scanOffset;
			scanOffset++;
			if (scanOffset > scanStep) {
				scanOffset = 0;
			}
		}

		if (heading <= minHeading) {
			heading = minHeading;
			scan();
			nextDirection = RIGHT;
			nextScanHeading = minHeading + scanStep + scanOffset;
			scanOffset++;
			if (scanOffset > scanStep) {
				scanOffset = 0;
			}
		}
		direction = nextDirection;
		return this;
	}

	private void scan() {
		log.info("SCAN: {}", heading);
	}

	public float getHeading() {
		return heading;
	}
}
