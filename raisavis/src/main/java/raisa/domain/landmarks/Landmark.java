package raisa.domain.landmarks;

import raisa.util.Vector2D;

public class Landmark {

	/* a life counter used to determine whether to discard a landmark */
    private int life;  

    private Vector2D position;
	
    /* start and end points for the line landmark */
    private Vector2D startPosition;
    private Vector2D endPosition;
        
}
