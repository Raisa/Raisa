package raisa.domain.landmarks;

import raisa.util.Vector2D;

public abstract class Landmark extends Vector2D {
	private static final long serialVersionUID = 1L;
	
	/* a life counter used to determine whether to discard a landmark */
    protected int life;  
        
}
