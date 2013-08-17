package raisa.ui.tool;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import raisa.domain.WorldModel;
import raisa.domain.plan.MotionPlan;
import raisa.domain.plan.Route;
import raisa.domain.plan.Waypoint;
import raisa.ui.VisualizerFrame;
import raisa.util.Vector2D;
import raisa.util.algorithm.search.GenericSearch;
import raisa.util.algorithm.search.SearchProblem;
import raisa.util.algorithm.search.SearchSolution;
import raisa.util.algorithm.search.SearchState;

public class WaypointTool extends BasicTool {

	private static final int MAXIMUM_DISTANCE_TO_SEARCH = 10000;
	private WorldModel world;
	 
	public WaypointTool(VisualizerFrame frame, WorldModel world) {
		super(frame);
		this.world = world;
	}

	private static class AStarGridSearchState implements SearchState {
		private double x;
		private double y;
		private double costSoFar;
		private double heuristic;
		private AStarGridSearchState previousState;

		public AStarGridSearchState(double x, double y, double costSoFar, double heuristic,
				AStarGridSearchState previousState) {
			this.x = x;
			this.y = y;
			this.costSoFar = costSoFar;
			this.heuristic = heuristic;
			this.previousState = previousState;
		}

		@Override
		public int compareTo(SearchState o) {
			AStarGridSearchState s = (AStarGridSearchState)o;
			return Double.compare(costSoFar + heuristic, s.costSoFar + s.heuristic);
		}

		@Override
		public SearchState getPreviousState() {
			return previousState;
		}

		@Override
		public String toString() {
			return "AStarGridSearchState [totalCost="+(costSoFar+heuristic)+", x=" + x + ", y=" + y + ", costSoFar="
					+ costSoFar + ", heuristic=" + heuristic + "]";
		}
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent, Vector2D mouse) {
		MotionPlan motionPlan = world.getMotionPlan();
		Route route = motionPlan.getSelectedRoute();
		Vector2D worldPosition = getVisualizerFrame().toWorld(mouse);
		
		Waypoint tempInitialState = null;
		if (route.getWaypoints().isEmpty()) {
			tempInitialState = new Waypoint(world.getLatestState().getEstimatedState().getPosition());
		} else {
			tempInitialState = route.getWaypoints().get(route.getWaypoints().size() - 1);
		}
		final Waypoint initialState = tempInitialState;
		final Waypoint goalState = new Waypoint(worldPosition);
		
		SearchProblem problem = new SearchProblem() {
			private Set<String> visitedSet = new HashSet<String>();
			
			@Override
			public SearchState getInitialState() {
				double x = initialState.getPosition().x;
				double y = initialState.getPosition().y;
				return new AStarGridSearchState(x, y, 0, heuristic(x, y), null);
			}

			private double heuristic(double x, double y) {
				double gx = goalState.getPosition().x;
				double gy = goalState.getPosition().y;
				double dx = gx - x;
				double dy = gy - y;
				return Math.sqrt(dx * dx + dy * dy);
			}

			@Override
			public boolean isGoalState(SearchState currentState) {
				AStarGridSearchState cs = (AStarGridSearchState)currentState;
				return heuristic(cs.x, cs.y) < world.getCellSize();
			}

			@Override
			public boolean shouldContinueSearch(SearchState currentState) {
				AStarGridSearchState cs = (AStarGridSearchState)currentState;
				return cs.costSoFar < MAXIMUM_DISTANCE_TO_SEARCH;
			}

			@Override
			public Collection<SearchState> getNeighbors(SearchState currentState) {
				List<SearchState> neighbors = new ArrayList<SearchState>();
				AStarGridSearchState cs = (AStarGridSearchState)currentState;
				double step = world.getCellSize();
				for (int dy = -1; dy <= 1; ++dy) {
					double y = cs.y + dy * step;
					for (int dx = -1; dx <= 1; ++dx) {
						if (dx == 0 && dy == 0) continue;
						double x = cs.x + dx * step;
						if (!world.isClear(new Vector2D((float)x, (float)y))) continue;
						double weight = 1.0;
						if (!world.isClear(new Vector2D((float)x, (float)y), 8.0f)) weight = 8.0;
						else if (!world.isClear(new Vector2D((float)x, (float)y), 16.0f)) weight = 2.0;
						double newCost = cs.costSoFar + weight * Math.sqrt(dx * dx * step * step + dy * dy * step * step);
						AStarGridSearchState newState = new AStarGridSearchState(x, y, newCost, heuristic(x, y), cs);
						neighbors.add(newState);
					}
				}
				
				return neighbors;
			}

			@Override
			public boolean isVisitedAlready(SearchState currentState) {
				AStarGridSearchState cs = (AStarGridSearchState)currentState;
				return visitedSet.contains(cs.x + "_" + cs.y);
			}

			@Override
			public void visit(SearchState currentState) {
				AStarGridSearchState cs = (AStarGridSearchState)currentState;
				visitedSet.add(cs.x + "_" + cs.y);
			}
			
		};
		GenericSearch search = new GenericSearch();
		SearchSolution solution = search.search(problem);
		
		SearchState currentState = solution.getFinalState();
		
		LinkedList<Waypoint> newWaypoints = new LinkedList<Waypoint>();
		
		while (currentState != null) {
			AStarGridSearchState cs = (AStarGridSearchState)currentState;
			newWaypoints.addFirst(new Waypoint(new Vector2D((float)cs.x, (float)cs.y)));
			currentState = cs.getPreviousState();
		}
		
		for (Waypoint w : newWaypoints) {
			route.addWaypoint(w);
		}
		
		if (solution.isFound()) {
			route.removeLastWaypoint();
			route.addWaypoint(goalState);
			// TODO add indication of failure
		}
		
		getVisualizerFrame().repaint();
	}

}
