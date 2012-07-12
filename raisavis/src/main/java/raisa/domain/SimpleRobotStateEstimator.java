package raisa.domain;

import java.util.List;


public class SimpleRobotStateEstimator implements RobotStateEstimator {
	@Override
	public Robot estimateState(List<Robot> states) {
		return states.get(0);
	}
}