package raisa.domain.landmarks;

import java.util.List;

import raisa.util.Vector2D;

public interface LandmarkExtractor {

	public List<Landmark> extractLandmarks(List<Vector2D> dataPoints);
	
}
