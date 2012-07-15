package raisa.domain;

public class NoFilter implements SampleListener {

	private SimpleRobotMovementEstimator robotMovementEstimator;
	private WorldModel worldModel;
	
	public NoFilter(WorldModel worldModel) {
		this.robotMovementEstimator = new SimpleRobotMovementEstimator(false);
		this.worldModel = worldModel;
	}
	
	@Override
	public void sampleAdded(Sample sample) {
		Robot estimatedState = robotMovementEstimator.moveRobot(worldModel.getLatestState(), sample);
		worldModel.addState(estimatedState);
	}	
	
}
