package raisa.domain;

import java.util.ArrayList;
import java.util.List;

import raisa.util.CollectionUtil;
import raisa.util.Vector2D;

public class Particle {
	private int maxStates = 10;
	private List<Robot> states = new ArrayList<Robot>();
	
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
			Sample sample = samples.get(i);
			Robot robot = states.get(i);
			if (!sample.isInfrared1MeasurementValid()) continue;
			
			float expectedDistance = world.traceRay(new Vector2D(robot.getPosition()), robot.getHeading() + sample.getInfrared1Angle());			
			float measuredDistance = sample.getInfrared1Distance();

			if (Math.max(expectedDistance, measuredDistance) <= 0.0f) continue;
			
			weights += Math.min(expectedDistance, measuredDistance) / Math.max(expectedDistance, measuredDistance); 
		}
		
		return weights;
	}

	public Robot getLastSample() {
		return states.isEmpty() ? null : states.get(states.size() - 1);
	}
}
