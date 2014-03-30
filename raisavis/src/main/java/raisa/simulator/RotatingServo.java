package raisa.simulator;


public class RotatingServo {
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
	private ServoScanListener servoScanListener;
	
	public RotatingServo(ServoScanListener servoScanListener) {
		this.heading = (minHeading + maxHeading) / 2.0f;
		this.servoScanListener = servoScanListener;
	}

	public RotatingServo rotate(float timestep) {
		heading += direction * timestep * rotatingSpeed;
		int nextDirection = direction;
		if (direction == LEFT && heading < nextScanHeading) {
			heading = nextScanHeading;
			scan(heading);
			nextScanHeading -= scanStep;
		}
		if (direction == RIGHT && heading > nextScanHeading) {
			heading = nextScanHeading;
			scan(heading);
			nextScanHeading += scanStep;
		}
		if (heading >= maxHeading) {
			heading = maxHeading;
			scan(heading);
			nextDirection = LEFT;
			nextScanHeading = maxHeading - scanStep - scanOffset;
			scanOffset++;
			if (scanOffset > scanStep) {
				scanOffset = 0;
			}
		}

		if (heading <= minHeading) {
			heading = minHeading;
			scan(heading);
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

	private void scan(float servoHeading) {
		servoScanListener.scan(servoHeading);
	}

	public float getHeading() {
		return heading;
	}
}
