package raisa.domain;

import raisa.util.Vector2D;

public class Robot {
	public final static float ROBOT_WIDTH = 10f;
	public final static float ROBOT_LENGTH = 20f;
	public final static float WHEEL_DIAMETER = 3.6f;
	public final static float TICK_RADIANS = (float) Math.PI / 8.0f;

	private long timestampMillis;
	private float heading;
	private Vector2D positionLeftTrack;
	private Vector2D positionRightTrack;
	private float speedLeftTrack;
	private float speedRightTrack;
	private boolean directionLeftTrackForward = true;
	private boolean directionRightTrackForward = true;

	public Robot() {
		this(new Vector2D(), (float) Math.PI);
	}

	public Robot(Vector2D position, float heading) {
		this.heading = heading;
		float tmpX = -ROBOT_WIDTH / 2.0f;
		float tmpY = 0.0f;
		this.positionLeftTrack = new Vector2D(position.x + (float) Math.cos(heading) * tmpX - (float) Math.sin(heading)
				* tmpY, position.y + (float) Math.sin(heading) * tmpX + (float) Math.cos(heading) * tmpY);
		tmpX = ROBOT_WIDTH / 2.0f;
		this.positionRightTrack = new Vector2D(position.x + (float) Math.cos(heading) * tmpX - (float) Math.sin(heading)
				* tmpY, position.y + (float) Math.sin(heading) * tmpX + (float) Math.cos(heading) * tmpY);
	}

	public Vector2D getPosition() {
		return new Vector2D((positionLeftTrack.x + positionRightTrack.x) / 2.0f,
				(positionLeftTrack.y + positionRightTrack.y) / 2.0f);
	}

	public Vector2D getPositionLeftTrack() {
		return new Vector2D(positionLeftTrack.x, positionLeftTrack.y);
	}

	public Vector2D getPositionRightTrack() {
		return new Vector2D(positionRightTrack.x, positionRightTrack.y);
	}

	public void setPositionLeftTrack(Vector2D positionLeftTrack) {
		this.positionLeftTrack = positionLeftTrack;
	}

	public void setPositionRightTrack(Vector2D positionRightTrack) {
		this.positionRightTrack = positionRightTrack;
	}

	public float getSpeedLeftTrack() {
		return speedLeftTrack;
	}

	public float getSpeedRightTrack() {
		return speedRightTrack;
	}

	public float getHeading() {
		return heading;
	}

	public long getTimestampMillis() {
		return timestampMillis;
	}

	public boolean isDirectionRightTrackForward() {
		return directionRightTrackForward;
	}

	public boolean isDirectionLeftTrackForward() {
		return directionLeftTrackForward;
	}

	public void setSpeedLeftTrack(float speed) {
		speedLeftTrack = speed;
	}

	public void setSpeedRightTrack(float speed) {
		speedRightTrack = speed;
	}

	public void setHeading(float heading) {
		this.heading = heading;
	}

	public void setTimestampMillis(long timestamp) {
		timestampMillis = timestamp;
	}

	public void setDirectionRightTrackForward(boolean direction) {
		directionRightTrackForward = direction;
	}

	public void setDirectionLeftTrackForward(boolean direction) {
		directionLeftTrackForward = direction;
	}

	@Override
	public String toString() {
		return "position (x=" + getPosition().x + ",y=" + getPosition().y + "), " + "lefttrack (x="
				+ positionLeftTrack.x + ",y=" + positionLeftTrack.y + "), " + "righttrack (x=" + positionRightTrack.x
				+ ",y=" + positionRightTrack.y + ")";
	}

}