package raisa.simulator;

public interface DriveSystem {
	DriveSystem setLeftWheelSpeed(int speed);

	DriveSystem setRightWheelSpeed(int speed);

	void move(RoverState roverState, float timestamp);
}
