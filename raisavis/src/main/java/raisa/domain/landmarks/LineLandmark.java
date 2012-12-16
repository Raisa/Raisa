package raisa.domain.landmarks;

import raisa.util.Segment2D;
import raisa.util.Vector2D;

public class LineLandmark extends Landmark {
	private static final long serialVersionUID = 1L;
	
	private Segment2D segment;	

	public LineLandmark(Segment2D segment) {
		this.segment = segment;
	}
	
	@Override
	public Vector2D getPosition() {
		// closest point of origin in line
		float apx = 0.0f - segment.x1;
		float apy = 0.0f - segment.y1;
		float abx = segment.x2 - segment.x1;
		float aby = segment.y2 - segment.y1;

		float ab2 = (float) (abx * abx + aby * aby);
		float ap_ab = (float) (apx * abx + apy * aby);
		float t = ap_ab / ab2;
		
		return new Vector2D(segment.x1 + abx * t, segment.y1 + aby * t);
	}
	
	public Segment2D getSegment() {
		return this.segment;
	} 
	
	@Override
	public void merge(Landmark landmark) {
		if (!(landmark instanceof LineLandmark)) {
			return;
		}
		LineLandmark mergedLandmark = (LineLandmark)landmark;
		Segment2D mergedSegment = mergedLandmark.getSegment();
		int lifeSum = life + mergedLandmark.getLife();
		segment.x1 = (segment.x1 * life + mergedSegment.x1 * mergedLandmark.getLife()) / lifeSum;
		segment.y1 = (segment.y1 * life + mergedSegment.y1 * mergedLandmark.getLife()) / lifeSum;
		segment.x2 = (segment.x2 * life + mergedSegment.x2 * mergedLandmark.getLife()) / lifeSum;
		segment.y2 = (segment.y2 * life + mergedSegment.y2 * mergedLandmark.getLife()) / lifeSum;
		segment.setSlope((segment.getSlope() * life + mergedSegment.getSlope() * mergedLandmark.getLife()) / lifeSum);
		segment.setIntersect((segment.getIntersect() * life + mergedSegment.getIntersect() * mergedLandmark.getLife()) / lifeSum);
	}

	@Override
	public boolean isTrusted() {
		return life > 1;
	}

}
