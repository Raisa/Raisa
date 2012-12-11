package raisa.domain.landmarks;

import java.util.List;

import raisa.domain.RobotState;
import raisa.domain.Sample;

public interface LandmarkExtractor {

	public List<Landmark> extractLandmarks(List<Sample> samples, List<RobotState> states);
	
}
