package raisa.util.algorithm.search;


public interface SearchState extends Comparable<SearchState> {
	SearchState getPreviousState();
}
