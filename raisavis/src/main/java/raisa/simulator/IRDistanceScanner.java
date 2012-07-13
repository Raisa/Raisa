package raisa.simulator;

import raisa.domain.WorldModel;

public class IRDistanceScanner implements DistanceScanner {

	@Override
	public float scanDistance(WorldModel worldModel, RobotState roverState, float heading) {
		return worldModel.traceRay(roverState.getPosition(), roverState.getHeading() + heading);
	}

}
