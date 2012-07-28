package raisa.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import raisa.util.Vector2D;

public class ClusteringRobotStateEstimator implements RobotStateEstimator {
	private final static class KMeansClustering {
		private List<Vector2D> clusterCenters = new ArrayList<Vector2D>();
		private Map<RobotState, Integer> clusterOfRobot = new HashMap<RobotState, Integer>();

		public boolean iterate(int k) {
			// assign robots to clusters
			boolean clusterChanged = false;
			for (RobotState state : clusterOfRobot.keySet()) {
				double bestDistanceSoFar = Double.MAX_VALUE;
				int oldCluster = clusterOfRobot.get(state);
				int newCluster = oldCluster;
				for (int i = 0; i < k; ++i) {
					double distance = clusterCenters.get(i).distanceSq(state.getPosition());
					if (distance < bestDistanceSoFar) {
						clusterOfRobot.put(state, i);
						newCluster = i;
						bestDistanceSoFar = distance;
					}
				}
				if (oldCluster != newCluster) {
					clusterChanged = true;
				}
			}
			// calculate new positions for clusters
			if (clusterChanged) {
				List<Vector2D> newClusterCenters = new ArrayList<Vector2D>();
				List<Integer> clusterPointCount = new ArrayList<Integer>();
				for (int i = 0; i < k; ++i) {
					newClusterCenters.add(new Vector2D());
					clusterPointCount.add(0);
				}
				for (RobotState state : clusterOfRobot.keySet()) {
					int cluster = clusterOfRobot.get(state);
					Vector2D center = newClusterCenters.get(cluster);
					Vector2D position = state.getPosition();
					center.x += position.x;
					center.y += position.y;
					int nRobotsInCluster = clusterPointCount.get(cluster);
					clusterPointCount.set(cluster, ++nRobotsInCluster);
				}
				for (int i = 0; i < k; ++i) {
					int nRobotsInCluster = clusterPointCount.get(i);
					if (nRobotsInCluster > 0) {
						Vector2D center = newClusterCenters.get(i);
						center.x /= nRobotsInCluster;
						center.y /= nRobotsInCluster;
					}
				}
				clusterCenters = newClusterCenters;
			}

			return clusterChanged;
		}

		public List<List<RobotState>> calculateClusters(List<RobotState> states, int k) {
			List<List<RobotState>> clusters = new ArrayList<List<RobotState>>();
			initClusterCenters(states, k);
			for (RobotState state : states) {
				clusterOfRobot.put(state, (int)(Math.random() * k));
			}
			while (iterate(k))
				;
			for (int i = 0; i < k; ++i) {
				clusters.add(new ArrayList<RobotState>());
				for (RobotState state : clusterOfRobot.keySet()) {
					if (i == clusterOfRobot.get(state)) {
						clusters.get(i).add(state);
					}
				}
			}
			// return in descending size order
			Collections.sort(clusters, new Comparator<List<RobotState>>() {
				@Override
				public int compare(List<RobotState> cluster1, List<RobotState> cluster2) {
					return cluster2.size() - cluster1.size();
				}
			});
			return clusters;
		}

		private void initClusterCenters(List<RobotState> states, int k) {
			List<RobotState> shuffledStates = shuffleStates(states);
			for (int i = 0; i < k; ++i) {
				clusterCenters.add(shuffledStates.get(i).getPosition());
			}
		}

		private List<RobotState> shuffleStates(List<RobotState> states) {
			List<RobotState> shuffledStates = new ArrayList<RobotState>(states);
			Collections.shuffle(shuffledStates);
			return shuffledStates;
		}
	}

	private AveragingRobotStateEstimator averagingRobotStateEstimator = new AveragingRobotStateEstimator();

	@Override
	public RobotState estimateState(List<RobotState> states) {
		KMeansClustering clustering = new KMeansClustering();
		List<List<RobotState>> clusters = clustering.calculateClusters(states, 3);
		List<RobotState> largestCluster = clusters.get(0);

		return averagingRobotStateEstimator.estimateState(largestCluster);
	}
}