package raisa.domain;

import java.awt.geom.Point2D.Float;

public class SimpleRobotMovementEstimator implements RobotMovementEstimator {

	@Override
	public Robot moveRobot(Robot state, Sample sample) {
		Robot r = new Robot();
		float leftTrackTrip = (Robot.WHEEL_DIAMETER * sample.getLeftTrackTicks() * Robot.TICK_RADIANS) / 2.0f;
		float rightTrackTrip = (Robot.WHEEL_DIAMETER * sample.getRightTrackTicks() * Robot.TICK_RADIANS) / 2.0f;

		float h = sample.getCompassDirection();
		Float positionLeftTrack = new Float(state.getPositionLeftTrack().x + leftTrackTrip * (float) Math.sin(h),
				state.getPositionLeftTrack().y - leftTrackTrip * (float) Math.cos(h));

		Float positionRightTrack = new Float(state.getPositionRightTrack().x + rightTrackTrip * (float) Math.sin(h),
				state.getPositionRightTrack().y - rightTrackTrip * (float) Math.cos(h));
		r.setTimestampMillis(sample.getTimestampMillis());

		r.setDirectionLeftTrackForward(sample.getLeftTrackTicks() >= 0 ? true : false);
		r.setDirectionRightTrackForward(sample.getRightTrackTicks() >= 0 ? true : false);
		
		// add noise
		positionLeftTrack.x += (float)Math.random() * 2.0f - 1.0f;
		positionLeftTrack.y += (float)Math.random() * 2.0f - 1.0f;
		positionRightTrack.x += (float)Math.random() * 2.0f - 1.0f;
		positionRightTrack.y += (float)Math.random() * 2.0f - 1.0f;
		h += (float)((Math.random() * 8.0f - 4.0f) / 180.0f * Math.PI);

		r.setPositionLeftTrack(positionLeftTrack);
		r.setPositionRightTrack(positionRightTrack);
		r.setHeading(h);

		return r;
	}
}
