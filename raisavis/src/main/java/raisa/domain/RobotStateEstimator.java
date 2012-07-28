package raisa.domain;

import java.util.List;

public interface RobotStateEstimator {

	RobotState estimateState(List<RobotState> states);

}
