package raisa.simulator;

public interface DriveSystem {
	DriveSystem setLeftWheelSpeed(float speed);

	DriveSystem setRightWheelSpeed(float speed);

	void move(RoverState roverState, float timestamp);
}
