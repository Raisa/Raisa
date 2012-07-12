package raisa.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.util.Vector2D;

/**
 * Simulated Rover.
 * 
 */
public class Rover implements RoverState {
	private static final Logger log = LoggerFactory.getLogger(Rover.class);
	/** degrees */
	private float heading;
	private Vector2D position;
	private RotatingServo rotatingServo = new RotatingServo();
	private DistanceScanner irScanner = new IRDistanceScanner();
	private DistanceScanner sonarScanner = new SonarDistanceScanner();
	private DriveSystem driveSystem;
	
	public Rover(Vector2D position, float heading, DriveSystem driveSystem) {
		this.position = position;
		this.heading = heading;
		this.driveSystem = driveSystem;
	}

	public Rover tick(float timestep) {
		driveSystem.move(this, timestep);
		log.info("Rover: {}, ({}, {})", new Object[]{heading, position.getX(), position.getY()});
		rotateScanners(timestep);
		return this;
	}
	
	private void rotateScanners(float timestep) {
		rotatingServo.rotate(timestep);
	}

	public float getIRDistance() {
		return irScanner.scanDistance(this, rotatingServo.getHeading() + getHeading());
	}

	public float getSonarDistance() {
		return sonarScanner.scanDistance(this, rotatingServo.getHeading() + getHeading());
	}

	@Override
	public float getHeading() {
		return heading;
	}

	@Override
	public void setHeading(float heading) {
		this.heading = heading % 360.0f;
	}

	@Override
	public Vector2D getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector2D position) {
		this.position = position;
	}
}
