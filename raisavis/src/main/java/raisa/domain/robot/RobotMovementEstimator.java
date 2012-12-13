package raisa.domain.robot;

import raisa.domain.samples.Sample;

public interface RobotMovementEstimator {
	RobotState moveRobot(RobotState state, Sample sample);
}
