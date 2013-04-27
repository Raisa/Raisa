package raisa.domain.plan;

import java.util.ArrayList;
import java.util.List;

public class Route {

	private List<Waypoint> waypoints;
	private int currentWaypointIndex;
	
	public Route() {
		this.waypoints = new ArrayList<Waypoint>();
	}
	
	public Waypoint getNextWaypoint() {
		if (waypoints == null || waypoints.size() <= currentWaypointIndex) {
			return null;
		} else {
			return waypoints.get(currentWaypointIndex);
		}
	}
	
	public void addWaypoint(Waypoint point) {
		waypoints.add(point);
	}
	
	public List<Waypoint> getWaypoints() {
		return this.waypoints;
	}
	
}
