package raisa.domain;

import java.awt.geom.Point2D.Float;
import java.util.List;


public class AveragingRobotStateEstimator implements RobotStateEstimator {
	@Override
	public Robot estimateState(List<Robot> states) {
		Robot averageState = new Robot();
		Float averageLeftTrack = new Float();
		Float averageRightTrack = new Float();
		float averageHeading = 0.0f;
		float ax = 0.0f;
		float ay = 0.0f;
		float scale = 1.0f / states.size();
		for (Robot state : states) {
			Float leftTrack = state.getPositionLeftTrack();
			averageLeftTrack.x += (leftTrack.x) * scale;
			averageLeftTrack.y += (leftTrack.y) * scale;
			Float rightTrack = state.getPositionRightTrack();
			averageRightTrack.x += (rightTrack.x) * scale;
			averageRightTrack.y += (rightTrack.y) * scale;
			ax += (float)Math.cos(state.getHeading());  
			ay += (float)Math.sin(state.getHeading());
		}
		averageHeading = (float)Math.atan2(ay, ax);
		averageState.setPositionLeftTrack(averageLeftTrack);
		averageState.setPositionRightTrack(averageRightTrack);
		averageState.setHeading(averageHeading);
		return averageState;
	}
}