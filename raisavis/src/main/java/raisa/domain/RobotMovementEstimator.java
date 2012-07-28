package raisa.domain;

public interface RobotMovementEstimator {
	RobotState moveRobot(RobotState state, Sample sample);
}
