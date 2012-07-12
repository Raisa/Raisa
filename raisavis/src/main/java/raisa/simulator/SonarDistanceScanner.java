package raisa.simulator;

import java.util.Arrays;
import java.util.List;

/**
 * Makes scans to several directions around central heading and returns the
 * shortest value.
 * 
 */
public class SonarDistanceScanner extends IRDistanceScanner {
	// simulate wide beam by doing several scans and taking the minimum distance
	private static final List<Float> beamHeadings = Arrays.asList(-10f, -8f, -6f, -2f, 0f, 2f, 4f, 6f, 8f, 10f); 

	@Override
	public float scanDistance(RoverState roverState, float heading) {
		float min = -1;
		for(float beamHeading: beamHeadings) {
			float distance = super.scanDistance(roverState, heading + beamHeading);
			if(distance > 0 && distance < min) {
				min = distance;
			}
		}
		return min;
	}

}
