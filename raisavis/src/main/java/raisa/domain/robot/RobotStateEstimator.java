package raisa.domain.robot;

import java.util.List;


public interface RobotStateEstimator {

	RobotState estimateState(List<RobotState> states);

}
