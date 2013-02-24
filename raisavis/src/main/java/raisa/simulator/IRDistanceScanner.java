package raisa.simulator;

import org.apache.commons.math3.distribution.NormalDistribution;

import raisa.domain.WorldModel;

public class IRDistanceScanner implements DistanceScanner {

	private NormalDistribution noise = new NormalDistribution(0.0d, 1.0d);
	
	@Override
	public float scanDistance(WorldModel worldModel, SimulatorState roverState, float heading) {
		float angle = 360 - roverState.getHeading() + heading;
		float rad = (float)Math.toRadians(angle);
		return worldModel.traceRay(roverState.getPosition(), rad) + (float)noise.sample();
	}

}
