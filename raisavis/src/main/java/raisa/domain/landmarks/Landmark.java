package raisa.domain.landmarks;

import raisa.util.Vector2D;

public abstract class Landmark extends Vector2D {
	private static final long serialVersionUID = 1L;
	
    protected int life = 1;  
    
    public void incLife() {
    	life++;
    }
    
    public void decLife() {
    	life--;
    }
    
    public int getLife() {
    	return life;
    }
    
    public boolean isSubtypeSame(Landmark landmark) {
    	return landmark.getClass().getCanonicalName().equals(this.getClass().getCanonicalName());
    }
        
    public abstract Vector2D getPosition();
    
    public abstract void merge(Landmark landmark);
    
}
