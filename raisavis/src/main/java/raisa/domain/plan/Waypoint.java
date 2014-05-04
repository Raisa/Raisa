package raisa.domain.plan;

import java.util.concurrent.atomic.AtomicInteger;

import raisa.util.Vector2D;

public class Waypoint {

	private static AtomicInteger waypointSeq = new AtomicInteger(0);

	private final Vector2D position;
	private final int id;
	private boolean reached;

	public Waypoint(Vector2D position) {
		this.position = position;
		this.id = waypointSeq.getAndIncrement();
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
