package raisa.simulator;

import raisa.util.Vector2D;

/**
 * Simulated Rover.
 * 
 */
public class Rover implements RoverState {
	/** degrees */
	private int heading;
	private Vector2D position;
	private RotatingServo rotatingServo = new RotatingServo();
	private DistanceScanner irScanner = new IRDistanceScanner();
	private DistanceScanner sonarScanner = new SonarDistanceScanner();
	private DriveSystem driveSystem;
	
	public Rover(Vector2D position, int heading, DriveSystem driveSystem) {
		this.position = position;
		this.heading = heading;
		this.driveSystem = driveSystem;
	}

	public Rover tick(float timestep) {
		driveSystem.move(this, timestep);
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
	public int getHeading() {
		return heading;
	}

	@Override
	public void setHeading(int heading) {
		this.heading = heading;
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
