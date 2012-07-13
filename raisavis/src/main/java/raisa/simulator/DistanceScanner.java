package raisa.simulator;

import raisa.domain.WorldModel;


public interface DistanceScanner {	
	float scanDistance(WorldModel worldModel, RobotState roverState, float heading);
}
