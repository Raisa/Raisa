package raisa.domain.plan;

import raisa.util.Vector2D;

public class Waypoint {
	
	private static int waypointSeq = 0;
	
	private Vector2D position;
	private int id;
	private boolean reached;
	
	public Waypoint(Vector2D position) {
		this.position = position;
		this.id = waypointSeq++;
		this.reached = false;
	}
	
	public Vector2D getPosition() {
		return this.position;
	}
	
	public void setReached(boolean reached) {
		this.reached = reached;
	}
	
	public boolean isReached() {
		return this.reached;
	}
	
	public int getId() {
		return this.id;
	}
	
}
