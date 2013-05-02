package raisa.domain.robot;


public class Robot {
	public final static float ROBOT_WIDTH = 12f;
	public final static float ROBOT_LENGTH = 20f;
	public final static float WHEEL_DIAMETER = 3.6f;
	public final static float TICK_RADIANS = (float) Math.PI / 8.0f;

	private long timestampMillis;
	private RobotState measuredState;
	private RobotState estimatedState;

	public Robot() {
		this.measuredState = new RobotState();
		this.estimatedState = new RobotState();		
	}
	
	public Robot(RobotState measuredState, RobotState estimatedState) {
		this.measuredState = measuredState;
		this.estimatedState = estimatedState;
	}
	
	public long getTimestampMillis() {
		return timestampMillis;
	}
	
	public RobotState getMeasuredState() {
		return measuredState;
	}
	
	public RobotState getEstimatedState() {
		return estimatedState;
	}
		
	public void setMeasuredState(RobotState state) {
		measuredState = state;
	}
	
	public void setEstimatedState(RobotState state) {
		estimatedState = state;
	}
	
	public void setTimestampMillis(long timestamp) {
		timestampMillis = timestamp;
	}
	
}