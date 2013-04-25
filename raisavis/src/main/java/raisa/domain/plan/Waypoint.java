package raisa.domain.plan;

import raisa.util.Vector2D;

public class Waypoint {

	private Vector2D position;
	
	public Waypoint(Vector2D position) {
		this.position = position;
	}
	
	public Vector2D getPosition() {
		return this.position;
	}
	
}
