package raisa.domain.landmarks;

import raisa.util.Segment2D;
import raisa.util.Vector2D;

public class LineLandmark extends Landmark {
	private static final long serialVersionUID = 1L;
	
	private Segment2D segment;    

    public LineLandmark(Segment2D segment) {
    	this.segment = segment;
    }

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
    
}
