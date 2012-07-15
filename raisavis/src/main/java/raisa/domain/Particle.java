package raisa.domain;

import java.util.ArrayList;
import java.util.List;

import raisa.util.CollectionUtil;

public class Particle {
	private int maxStates = 50;
	private List<Robot> states = new ArrayList<Robot>();
	
	public Particle copy() {
		Particle newParticle = new Particle();
		newParticle.maxStates = maxStates;
		newParticle.states = new ArrayList<Robot>(states);
		return newParticle;
	}

	public void addState(Robot robot) {
		states.add(robot);
		states = CollectionUtil.takeLast(states, maxStates);
	}
	
	public float calculateWeight(WorldModel world, List<Sample> samples) {
		int windowLength = Math.min(states.size(), samples.size());
		if (samples.size() != windowLength) {
			samples = CollectionUtil.takeLast(samples, windowLength);
		}
		List<Robot> states = this.states;
		if (states.size() != windowLength) {
			states = CollectionUtil.takeLast(states, windowLength);
		}
		
		float weights = 0.0f;
		
		for (int i = 0; i < windowLength; ++i) {
			// weight based on samples
			Sample sample = samples.get(i);
			Robot state = states.get(i);
			if (!sample.isInfrared1MeasurementValid()) continue;
			
			float expectedDistance = world.traceRay(state.getPosition(), state.getHeading() + sample.getInfrared1Angle());			
			float measuredDistance = sample.getInfrared1Distance();

			if (Math.max(expectedDistance, measuredDistance) <= 0.0f) continue;
			
			float ratio = Math.min(expectedDistance, measuredDistance) / Math.max(expectedDistance, measuredDistance);
			//weights += ratio * ratio;
			weights += ratio; 

			// weight based on compass reading (take angle between unit vectors)
			float cosa = (float)(Math.cos(state.getHeading()) * Math.cos(sample.getCompassDirection()) + Math.sin(state.getHeading()) * Math.sin(sample.getCompassDirection()));
			// cosinus is -1..+1 and near 1 when angles are close to each other, scale to 0..1
			weights += (1.0f + cosa) * 0.5f;
		}
		
		
		return weights;
	}

	public Robot getLastState() {
		return states.isEmpty() ? null : states.get(states.size() - 1);
	}
}
