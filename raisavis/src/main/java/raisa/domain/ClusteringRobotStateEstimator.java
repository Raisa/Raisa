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
		private Map<Robot, Integer> clusterOfRobot = new HashMap<Robot, Integer>();

		public boolean iterate(int k) {
			// assign robots to clusters
			boolean clusterChanged = false;
			for (Robot state : clusterOfRobot.keySet()) {
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
				for (Robot state : clusterOfRobot.keySet()) {
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

		public List<List<Robot>> calculateClusters(List<Robot> states, int k) {
			List<List<Robot>> clusters = new ArrayList<List<Robot>>();
			initClusterCenters(states, k);
			for (Robot state : states) {
				clusterOfRobot.put(state, (int)(Math.random() * k));
			}
			while (iterate(k))
				;
			for (int i = 0; i < k; ++i) {
				clusters.add(new ArrayList<Robot>());
				for (Robot state : clusterOfRobot.keySet()) {
					if (i == clusterOfRobot.get(state)) {
						clusters.get(i).add(state);
					}
				}
			}
			// return in descending size order
			Collections.sort(clusters, new Comparator<List<Robot>>() {
				@Override
				public int compare(List<Robot> cluster1, List<Robot> cluster2) {
					return cluster2.size() - cluster1.size();
				}
			});
			return clusters;
		}

		private void initClusterCenters(List<Robot> states, int k) {
			List<Robot> shuffledStates = shuffleStates(states);
			for (int i = 0; i < k; ++i) {
				clusterCenters.add(shuffledStates.get(i).getPosition());
			}
		}

		private List<Robot> shuffleStates(List<Robot> states) {
			List<Robot> shuffledStates = new ArrayList<Robot>(states);
			Collections.shuffle(shuffledStates);
			return shuffledStates;
		}
	}

	private AveragingRobotStateEstimator averagingRobotStateEstimator = new AveragingRobotStateEstimator();

	@Override
	public Robot estimateState(List<Robot> states) {
		KMeansClustering clustering = new KMeansClustering();
		List<List<Robot>> clusters = clustering.calculateClusters(states, 3);
		List<Robot> largestCluster = clusters.get(0);

		return averagingRobotStateEstimator.estimateState(largestCluster);
	}
}