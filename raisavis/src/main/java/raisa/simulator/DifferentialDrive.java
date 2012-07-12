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
	private final float motorSpeedToDistance;

	public DifferentialDrive(float axisWidth, float motorSpeedToDistance) {
		this.axisWidth = axisWidth;
		this.motorSpeedToDistance = motorSpeedToDistance;
	}

	@Override
	public void move(RoverState roverState, float timestep) {
		double theta0 = toRadians(roverState.getHeading());
		double rightTravelDistance = rightSpeed * motorSpeedToDistance * timestep;
		double leftTravelDistance = leftSpeed * motorSpeedToDistance * timestep;

		double avgTravelDistance = (rightTravelDistance + leftTravelDistance) / 2f;
		double theta = (rightTravelDistance - leftTravelDistance) * timestep / axisWidth + theta0;
		double newX = avgTravelDistance * Math.cos(theta) + roverState.getPosition().x;
		double newY = avgTravelDistance * Math.sin(theta) + roverState.getPosition().y;

		roverState.setHeading(toDegrees(theta));
		roverState.getPosition().setLocation(newX, newY);
	}

	private double toRadians(float degrees) {
		return degrees * 2 * Math.PI / 360f;
	}

	private float toDegrees(double radians) {
		return (float) (radians / 2.0 / Math.PI * 360f);
	}

	@Override
	public DriveSystem setLeftWheelSpeed(float speed) {
		this.leftSpeed = speed;
		return this;
	}

	@Override
	public DriveSystem setRightWheelSpeed(float speed) {
		this.rightSpeed = speed;
		return this;
	}

}
