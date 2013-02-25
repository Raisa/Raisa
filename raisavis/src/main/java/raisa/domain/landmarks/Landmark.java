package raisa.domain.landmarks;

import raisa.util.Vector2D;

public abstract class Landmark extends Vector2D {
	private static final long serialVersionUID = 1L;
	
    protected int life = 1;  
    protected Integer slamId;   
    private Landmark detectedLandmark;
    private Vector2D adjustedPosition;
    
    public void setAdjustedPosition(Vector2D adjustedPosition) {
    	this.adjustedPosition = adjustedPosition;
    }
    
    public Vector2D getAdjustedPosition() {
    	return this.adjustedPosition;
    }
    
    public int getLife() {
    	return life;
    }
    
    public void incLife() {
    	life++;
    }
    
	public void setDetectedLandmark(Landmark detectedLandmark) {
		this.detectedLandmark = detectedLandmark;
	}
	
	public Landmark getDetectedLandmark() {
		return this.detectedLandmark;
	}

    
    public void setSlamId(Integer slamId) {
    	this.slamId = slamId;
    }
    
    public Integer getSlamId() {
    	return this.slamId;
    }
    
    public boolean isSubtypeSame(Landmark landmark) {
    	return landmark.getClass().getCanonicalName().equals(this.getClass().getCanonicalName());
    }
        
    public abstract Vector2D getPosition();
    
    public abstract void merge(Landmark landmark);
    
    public abstract boolean isTrusted();
    
}
