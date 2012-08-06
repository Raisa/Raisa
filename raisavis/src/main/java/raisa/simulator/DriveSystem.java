package raisa.simulator;

public interface DriveSystem {
	DriveSystem setLeftWheelSpeed(float speed);

	DriveSystem setRightWheelSpeed(float speed);

	void move(SimulatorState roverState, float timestep);
	
	int readLeftWheelEncoderTicks();
	
	int readRightWheelEncoderTicks();
}
