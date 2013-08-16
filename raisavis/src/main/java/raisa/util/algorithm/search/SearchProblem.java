package raisa.util.algorithm.search;

import java.util.Collection;

public interface SearchProblem {

	SearchState getInitialState();

	boolean isGoalState(SearchState currentState);

	boolean shouldContinueSearch(SearchState currentState);

	Collection<SearchState> getNeighbors(SearchState currentState);

	boolean isVisitedAlready(SearchState currentState);

	void visit(SearchState currentState);

}
