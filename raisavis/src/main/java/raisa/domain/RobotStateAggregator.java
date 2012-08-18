package raisa.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.config.VisualizerConfig;
import raisa.util.CollectionUtil;
import raisa.util.Vector2D;

public class RobotStateAggregator implements SampleListener {

	private static final Logger log = LoggerFactory.getLogger(RobotStateAggregator.class);
	
	private SimpleRobotMovementEstimator simpleRobotMovementEstimator;
	private ClusteringRobotStateEstimator clusteringRobotStateEstimator;;
	private ParticleFilter particleFilter;
	private WorldModel world;
	
	public RobotStateAggregator(WorldModel world, ParticleFilter particleFilter) {
		this.simpleRobotMovementEstimator = new SimpleRobotMovementEstimator(false);
		this.clusteringRobotStateEstimator = new ClusteringRobotStateEstimator();
		this.particleFilter = particleFilter;
		this.world = world;
	}
	
	@Override
	public void sampleAdded(Sample sample) {
		RobotState measuredState, estimatedState;	
		
		// calculate new robot state straight from the measurements
		Robot lastRobot = world.getLatestState();
		measuredState = simpleRobotMovementEstimator.moveRobot(lastRobot.getMeasuredState(), sample);
		measuredState.setOdometer(calculateOdometer(measuredState, lastRobot.getMeasuredState()));
		
		// calculate new robot state using particle filter
		switch (VisualizerConfig.getInstance().getLocalizationMode()) {
		case PARTICLE_FILTER:
			particleFilter.updateParticles(sample);
			List<RobotState> states = new ArrayList<RobotState>();
			for (Particle particle : particleFilter.getParticles()) {
				RobotState robot = particle.getLastState();
				states.add(robot);
			}
			estimatedState = clusteringRobotStateEstimator.estimateState(states);
			estimatedState.setOdometer(calculateOdometer(estimatedState, lastRobot.getEstimatedState()));
			break;
		default:
			estimatedState = measuredState;
		}
		
		// aggregate robot states and misc state calculations
		Robot newRobot = new Robot(measuredState, estimatedState);
		newRobot.setTimestampMillis(sample.getTimestampMillis());
		calculateSpeed(newRobot);
		world.addState(newRobot);
	}	
	
	private float calculateOdometer(RobotState newState, RobotState oldState) {
		return oldState.getOdometer() + (float)newState.getPosition().distance(oldState.getPosition());
	}
	
	/**
	 * Calculate speed for tracks based on locations and timestamps of the previous states. Needs some refactoring.
	 */
	private void calculateSpeed(Robot robot) {
		float currentMeasuredSpeedLeftTrack = 0.0f, currentMeasuredSpeedRightTrack = 0.0f,
				currentEstimatedSpeedLeftTrack = 0.0f, currentEstimatedSpeedRightTrack = 0.0f;
		List<Robot> pastStates = CollectionUtil.takeLast(world.getStates(), 5);
		if (pastStates.size() > 1) {
			boolean isFirst = true;
			Vector2D previousMeasuredPositionLeftTrack = new Vector2D(), previousMeasuredPositionRightTrack = new Vector2D(),
					previousEstimatedPositionLeftTrack = new Vector2D(), previousEstimatedPositionRightTrack = new Vector2D();
			long accumulatedTime = 0, previousTimestamp = 0;
			float accumulatedMeasuredDistanceLeftTrack = 0.0f, accumulatedMeasuredDistanceRightTrack = 0.0f,
					accumulatedEstimatedDistanceLeftTrack = 0.0f, accumulatedEstimatedDistanceRightTrack = 0.0f;
			for (Robot r : pastStates) {
				RobotState measuredState = r.getMeasuredState();
				RobotState estimatedState = r.getEstimatedState();
				if (isFirst) {
					isFirst = false;
				} else {
					accumulatedMeasuredDistanceLeftTrack += (measuredState.isDirectionLeftTrackForward() ? 1.0f : -1.0f) * previousMeasuredPositionLeftTrack.distance(measuredState.getPositionLeftTrack());
					accumulatedMeasuredDistanceRightTrack += (measuredState.isDirectionRightTrackForward() ? 1.0f : -1.0f) * previousMeasuredPositionRightTrack.distance(measuredState.getPositionRightTrack());
					accumulatedEstimatedDistanceLeftTrack += (estimatedState.isDirectionLeftTrackForward() ? 1.0f : -1.0f) * previousEstimatedPositionLeftTrack.distance(estimatedState.getPositionLeftTrack());
					accumulatedEstimatedDistanceRightTrack += (estimatedState.isDirectionRightTrackForward() ? 1.0f : -1.0f) * previousEstimatedPositionRightTrack.distance(estimatedState.getPositionRightTrack());
					accumulatedTime += r.getTimestampMillis() - previousTimestamp;
				}
				previousMeasuredPositionLeftTrack = measuredState.getPositionLeftTrack();
				previousMeasuredPositionRightTrack = measuredState.getPositionRightTrack();
				previousEstimatedPositionLeftTrack = estimatedState.getPositionLeftTrack();
				previousEstimatedPositionRightTrack = estimatedState.getPositionRightTrack();
				previousTimestamp = r.getTimestampMillis();				
			}
			currentMeasuredSpeedLeftTrack = accumulatedMeasuredDistanceLeftTrack / (accumulatedTime / 10.0f);
			currentMeasuredSpeedRightTrack = accumulatedMeasuredDistanceRightTrack / (accumulatedTime / 10.0f);			
			currentEstimatedSpeedLeftTrack = accumulatedEstimatedDistanceLeftTrack / (accumulatedTime / 10.0f);
			currentEstimatedSpeedRightTrack = accumulatedEstimatedDistanceRightTrack / (accumulatedTime / 10.0f);			
		}
		robot.getMeasuredState().setSpeedLeftTrack(currentMeasuredSpeedLeftTrack);
		robot.getMeasuredState().setSpeedRightTrack(currentMeasuredSpeedRightTrack);
		robot.getEstimatedState().setSpeedLeftTrack(currentEstimatedSpeedLeftTrack);
		robot.getEstimatedState().setSpeedRightTrack(currentEstimatedSpeedRightTrack);		
	}
		
}
