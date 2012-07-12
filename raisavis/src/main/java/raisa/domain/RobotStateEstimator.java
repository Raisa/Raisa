package raisa.domain;

import java.util.List;

public interface RobotStateEstimator {

	Robot estimateState(List<Robot> states);

}
