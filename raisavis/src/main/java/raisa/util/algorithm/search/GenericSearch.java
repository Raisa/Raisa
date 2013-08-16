package raisa.util.algorithm.search;

import java.util.PriorityQueue;

public class GenericSearch {
	private static class GenericSearchSolution implements SearchSolution {
		private SearchState finalState;
		
		public SearchState getFinalState() {
			return finalState;
		}

		public void setFinalState(SearchState finalState) {
			this.finalState = finalState;
		}

		@Override
		public boolean isFound() {
			return finalState != null;
		}
	}
	
	public SearchSolution search(SearchProblem problem) {
		GenericSearchSolution solution = new GenericSearchSolution();
		
		PriorityQueue<SearchState> unsearched = new PriorityQueue<SearchState>();
		
		unsearched.add(problem.getInitialState());
		
		while (!unsearched.isEmpty()) {
			SearchState currentState = unsearched.remove();
			
			if (problem.isVisitedAlready(currentState)) continue;
			
			problem.visit(currentState);
			
			if (problem.isGoalState(currentState)) {
				solution.setFinalState(currentState);
				break;
			}
			
			if (!problem.shouldContinueSearch(currentState)) {
				break;
			}
			
			unsearched.addAll(problem.getNeighbors(currentState));
		}
		
		return solution;
	}
}
