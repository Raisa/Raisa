package raisa.simulator;


/**
 * 
 * @see http://rossum.sourceforge.net/papers/DiffSteer/
 * 
 */
public class DifferentialDrive implements DriveSystem {
	private float leftSpeed;
	private float rightSpeed;
	private final float axisWidth;
	private final float wheelDiameter;
	public DifferentialDrive(float axisWidth, float wheelDiameter) {
		this.axisWidth = axisWidth;
		this.wheelDiameter = wheelDiameter;
	}

	@Override
	public void move(RobotState roverState, float timestep) {
		double theta0 = Math.toRadians(roverState.getHeading());
		double wheelDistance = Math.PI * wheelDiameter; 
		double rightTravelDistance = rightSpeed * wheelDistance * timestep;
		double leftTravelDistance = leftSpeed * wheelDistance * timestep;

		double avgTravelDistance = (rightTravelDistance + leftTravelDistance) / 2f;
		double theta = (rightTravelDistance - leftTravelDistance) * timestep / axisWidth + theta0;
		double newX = avgTravelDistance * Math.cos(theta) + roverState.getPosition().x;
		double newY = avgTravelDistance * Math.sin(theta) + roverState.getPosition().y;

		roverState.setHeading((float)Math.toDegrees(theta));
		roverState.getPosition().setLocation(newX, newY);
	}

	/**
	 * Rotations per second.
	 */
	@Override
	public DriveSystem setLeftWheelSpeed(float speed) {
		this.leftSpeed = speed;
		return this;
	}

	/**
	 * Rotations per second.
	 */
	@Override
	public DriveSystem setRightWheelSpeed(float speed) {
		this.rightSpeed = speed;
		return this;
	}

}
