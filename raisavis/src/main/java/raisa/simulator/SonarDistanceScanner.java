package raisa.simulator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import raisa.domain.WorldModel;
import raisa.util.RandomUtil;

/**
 * Makes scans to several directions around central heading and returns the
 * shortest value.
 * 
 */
public class SonarDistanceScanner extends IRDistanceScanner {
	// simulate wide beam by doing several scans and taking the minimum distance
	private static final List<Float> beamHeadings = Arrays.asList(-5f, -2.5f, 0f, 2.5f, 5f); 
	
	private NormalDistribution noise = RandomUtil.normalDistribution(0.0d, 1.0d);

	@Override
	public float scanDistance(WorldModel worldModel, SimulatorState roverState, float heading) {
		float min = -1;
		for (float beamHeading : beamHeadings) {
			float distance = super.scanDistance(worldModel, roverState, heading + beamHeading);
			if(min < 0 || (distance > 0 && distance < min)) {
				min = distance;
			}
		}
		return min + (float)noise.sample();
	}

}
