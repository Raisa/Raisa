package raisa.domain.particlefilter;

import java.util.ArrayList;
import java.util.List;

import raisa.domain.WorldModel;
import raisa.domain.robot.RobotState;
import raisa.domain.samples.Sample;
import raisa.util.CollectionUtil;

public class Particle {
	private int maxStates = 15;
	private List<RobotState> states = new ArrayList<RobotState>();
	private int age = 0;

	public Particle copy() {
		Particle newParticle = new Particle();
		newParticle.maxStates = maxStates;
		newParticle.states = new ArrayList<RobotState>(states);
		newParticle.age =  ++age;
		return newParticle;
	}

	public void addState(RobotState robot) {
		states.add(robot);
		states = CollectionUtil.takeLast(states, maxStates);
	}
	
	public float calculateWeight(WorldModel world, List<Sample> samples) {
		int windowLength = Math.min(states.size(), samples.size());
		int missingStatesCount = Math.max(states.size(), samples.size()) - windowLength;

		if (samples.size() != windowLength) {
			samples = CollectionUtil.takeLast(samples, windowLength);
		}
		List<RobotState> states = this.states;
		if (states.size() != windowLength) {
			states = CollectionUtil.takeLast(states, windowLength);
		}
		
		float weights = 0.0f;
		
		for (int i = 0; i < windowLength; ++i) {
			
			// weight based on samples
			Sample sample = samples.get(i);
			RobotState state = states.get(i);
			
			if (sample.isInfrared1MeasurementValid()) {
				weights += calculateSingleWeight(world, state, sample.getCompassDirection(), sample.getInfrared1Angle(), sample.getInfrared1Distance(), sample.isUltrasound1MeasurementValid());
			} 
			if (sample.isUltrasound1MeasurementValid()) {
				weights += calculateSingleWeight(world, state, sample.getCompassDirection(), sample.getUltrasound1Angle(), sample.getUltrasound1Distance(), sample.isInfrared1MeasurementValid());
			}
			if (sample.isInfrared2MeasurementValid()) {
				weights += calculateSingleWeight(world, state, sample.getCompassDirection(), sample.getInfrared2Angle(), sample.getInfrared2Distance(), sample.isUltrasound2MeasurementValid());
			} 
			if (sample.isUltrasound2MeasurementValid()) {
				weights += calculateSingleWeight(world, state, sample.getCompassDirection(), sample.getUltrasound2Angle(), sample.getUltrasound2Distance(), sample.isInfrared2MeasurementValid());
			}
		}
		return weights + (0.2f * missingStatesCount * (weights / (float)windowLength));
	}
	
	private float calculateSingleWeight(WorldModel world, RobotState state, float compassDirection, float angle, float distance, boolean otherSensorMeasurementValid) {
		float expectedDistance = world.traceRay(state.getPosition(), state.getHeading() + angle);			
		float measuredDistance = distance;	
		float ratio = Math.min(expectedDistance, measuredDistance) / Math.max(expectedDistance, measuredDistance);

		// weight based on compass reading (take angle between unit vectors)
		float cosa = (float)(Math.cos(state.getHeading()) * Math.cos(compassDirection) + Math.sin(state.getHeading()) * Math.sin(compassDirection));
		// cosinus is -1..+1 and near 1 when angles are close to each other, scale to 0..1
		return (ratio * ratio + 0.05f * (1.0f + cosa) * (1.0f + cosa)) * (otherSensorMeasurementValid ? 1.0f : 1.5f);
	}

	public RobotState getLastState() {
		return states.isEmpty() ? null : states.get(states.size() - 1);
	}

	public int getAge() {
		return age;
	}
}
