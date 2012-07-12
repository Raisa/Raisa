package raisa.domain;

import raisa.util.Vector2D;

public class SimpleRobotMovementEstimator implements RobotMovementEstimator {

	@Override
	public Robot moveRobot(Robot state, Sample sample) {
		Robot robot = new Robot();
		float leftTrackTrip = (Robot.WHEEL_DIAMETER * sample.getLeftTrackTicks() * Robot.TICK_RADIANS) / 2.0f;
		float rightTrackTrip = (Robot.WHEEL_DIAMETER * sample.getRightTrackTicks() * Robot.TICK_RADIANS) / 2.0f;

		float h = state.getHeading();
		Vector2D positionLeftTrack = new Vector2D(state.getPositionLeftTrack().x + leftTrackTrip * (float) Math.sin(h),
				state.getPositionLeftTrack().y - leftTrackTrip * (float) Math.cos(h));

		Vector2D positionRightTrack = new Vector2D(state.getPositionRightTrack().x + rightTrackTrip * (float) Math.sin(h),
				state.getPositionRightTrack().y - rightTrackTrip * (float) Math.cos(h));
		
		robot.setTimestampMillis(sample.getTimestampMillis());
		robot.setDirectionLeftTrackForward(sample.getLeftTrackTicks() >= 0 ? true : false);
		robot.setDirectionRightTrackForward(sample.getRightTrackTicks() >= 0 ? true : false);
		
		// add noise		
		float noiseMagnitude = 5.0f;
		float a = (float)(Math.random() * Math.PI * 2.0f);
		float r = (float)Math.random() * noiseMagnitude;
		positionLeftTrack.x += (float)Math.cos(a) * r;
		positionLeftTrack.y += (float)Math.sin(a) * r;
		positionRightTrack.x += (float)Math.cos(a) * r;
		positionRightTrack.y += (float)Math.sin(a) * r;
		h += (float)((Math.random() * 8.0f - 4.0f) / 180.0f * Math.PI);

		robot.setPositionLeftTrack(positionLeftTrack);
		robot.setPositionRightTrack(positionRightTrack);
		robot.setHeading(h);

		return robot;
	}
}
