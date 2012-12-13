package raisa.domain.robot;

import java.util.List;

import raisa.util.Vector2D;

public class AveragingRobotStateEstimator implements RobotStateEstimator {
	@Override
	public RobotState estimateState(List<RobotState> states) {
		RobotState averageState = new RobotState();
		Vector2D averageLeftTrack = new Vector2D();
		Vector2D averageRightTrack = new Vector2D();
		float averageHeading = 0.0f;
		float ax = 0.0f;
		float ay = 0.0f;
		float scale = 1.0f / states.size();
		for (RobotState state : states) {
			Vector2D leftTrack = state.getPositionLeftTrack();
			averageLeftTrack.x += (leftTrack.x) * scale;
			averageLeftTrack.y += (leftTrack.y) * scale;
			Vector2D rightTrack = state.getPositionRightTrack();
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