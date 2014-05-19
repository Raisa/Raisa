package raisa.domain.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * Member methods are synchronized, because Route may remove added waypoints autonomously
 * if they're redundant. Access to waypoints-list must be prevented during the adjustment process.
 */
public class Route {

	private final List<Waypoint> waypoints;
	private int currentWaypointIndex;

	public Route() {
		this.waypoints = new ArrayList<Waypoint>();
	}

	public synchronized Waypoint getNextWaypoint() {
		if (waypoints == null || waypoints.size() <= currentWaypointIndex) {
			return null;
		} else {
			return waypoints.get(currentWaypointIndex);
		}
	}

	public synchronized void addWaypoint(Waypoint point) {
		waypoints.add(point);
		removeRedundantWaypoints();
	}

	/**
	 * Loop through the waypoints starting from the end. If three points are in
	 * the same line, the middle point is redundant.
	 */
	private void removeRedundantWaypoints() {
		if (currentWaypointIndex > waypoints.size() - 3) {
			return;
		}
		SimpleRegression sr = new SimpleRegression();
		addRegressionPoint(sr, waypoints.size() - 1);
		addRegressionPoint(sr, waypoints.size() - 2);
		int i = waypoints.size() - 3;
		while (i >= currentWaypointIndex) {
			addRegressionPoint(sr, i);
			Double error = sr.getMeanSquareError();
			if (error != Double.NaN && error > 0.001d) {
				break;
			}
			waypoints.remove(i+1);
			i--;
		}
	}

	private void addRegressionPoint(SimpleRegression sr, int index) {
		sr.addData(waypoints.get(index).getPosition().getX(), waypoints.get(index).getPosition().getY());
	}

	public synchronized List<Waypoint> getWaypoints() {
		return this.waypoints;
	}

	public synchronized void moveToNextWaypoint() {
		currentWaypointIndex++;
	}

	public synchronized void removeLastWaypoint() {
		waypoints.remove(waypoints.size() - 1);
	}

	public synchronized boolean isEmpty() {
		return waypoints.isEmpty();
	}

}
