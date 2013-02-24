package raisa.domain.landmarks;

import java.util.ArrayList;
import java.util.List;

import raisa.config.VisualizerConfig;
import raisa.domain.AlgorithmTypeEnum;
import raisa.domain.robot.Robot;
import raisa.domain.robot.RobotState;
import raisa.domain.samples.Sample;
import raisa.util.CollectionUtil;
import raisa.util.Vector2D;

public class LandmarkManager {

	private static final int RANSAC_SAMPLES = 200;
	private static final int SPIKE_SAMPLES = 50;
	
	private static final int RECALCULATE_INTERVAL = 200;
	
	private static final float ASSOCIATION_THRESHOLD = 60.0f;
	
	private List<Landmark> landmarks = new ArrayList<Landmark>();
	
	private List<Vector2D> dataPoints = new ArrayList<Vector2D>();
	private List<Sample> samples = new ArrayList<Sample>();
	private List<Robot> states = new ArrayList<Robot>();
	private int sampleCounter = 0;
	
	private RansacExtractor ransacExtractor = new RansacExtractor();
	private SpikeExtractor spikeExtractor = new SpikeExtractor();

	private VisualizerConfig config = VisualizerConfig.getInstance();
	
	public void reset() {
		landmarks = new ArrayList<Landmark>();
		dataPoints = new ArrayList<Vector2D>();
		samples = new ArrayList<Sample>();
		states = new ArrayList<Robot>();
		sampleCounter = 0;
		ransacExtractor.reset();
	}
	
	public List<Landmark> getLandmarks() {
		return this.landmarks;
	}
	
	public boolean addData(Sample sample, Robot state) {
		boolean ret = false;
		if (sample == null || state == null) {
			return ret;
		}
		boolean executeRansac = config.getActivatedAlgorithms().contains(AlgorithmTypeEnum.RANSAC_LANDMARK_EXTRACTION);
		boolean executeSpikes = config.getActivatedAlgorithms().contains(AlgorithmTypeEnum.SPIKES_LANDMARK_EXTRACTION);
		if (!executeRansac && !executeSpikes) {
			return ret;
		}
		sampleCounter++;
		dataPoints.addAll(extractPoints(sample, state));
		samples.add(sample);
		states.add(state);
		if (sampleCounter % RECALCULATE_INTERVAL == 0) {
			for (Landmark landmark : landmarks) {
				landmark.setDetectedLandmark(null);
			}
			if (executeRansac) {
				landmarks.addAll(
						associateLandmarks(
								ransacExtractor.extractLandmarks(
										CollectionUtil.takeLast(dataPoints, 4 * RANSAC_SAMPLES))));
			} 
			if (executeSpikes) {
				landmarks.addAll(
						associateLandmarks(
								spikeExtractor.extractLandmarks(
										CollectionUtil.takeLast(samples, SPIKE_SAMPLES),
										CollectionUtil.takeLast(states, SPIKE_SAMPLES))));			
			}
			ret = executeRansac || executeSpikes;
		}
		return ret;
	}
	
	private List<Landmark> associateLandmarks(List<Landmark> landmarkProspects) {
		List<Landmark> mergedProspects = new ArrayList<Landmark>(landmarkProspects);
		List<Landmark> newLandmarks = new ArrayList<Landmark>();
				
		// merge landmark prospects with each other
		boolean foundMerge = true;
		while (foundMerge) {
			foundMerge = false;
			Landmark m1 = null, m2 = null;
			for (Landmark prospect : mergedProspects) {
				for (Landmark anotherProspect : mergedProspects) {
					if (prospect != anotherProspect) {
						if (prospect.isSubtypeSame(anotherProspect) &&
							prospect.getPosition().distance(anotherProspect.getPosition()) < ASSOCIATION_THRESHOLD) {
							m1 = prospect;
							m2 = anotherProspect;
							foundMerge = true;
							break;
						}
					}
				}
				if (foundMerge) {
					break;
				}
			}
			if (foundMerge) {
				mergedProspects.remove(m2);
				m1.merge(m2);
			}
		}
		
		// merge prospects with existing landmarks
		for (Landmark prospect : mergedProspects) {
			Landmark bestAssociation = null;
			float distanceToBestAssociation = ASSOCIATION_THRESHOLD;
			for (Landmark existingLandmark : landmarks) {
				if ((prospect instanceof LineLandmark && existingLandmark instanceof LineLandmark) || 
					(prospect instanceof SpikeLandmark && existingLandmark instanceof SpikeLandmark)) {
					if (distanceToBestAssociation > prospect.getPosition().distance(existingLandmark.getPosition())) {
						bestAssociation = existingLandmark;
						distanceToBestAssociation = (float) prospect.getPosition().distance(existingLandmark);
					}
				}
			}
			if (bestAssociation == null) {
				newLandmarks.add(prospect);
			} else if (!bestAssociation.isTrusted()) {
				bestAssociation.merge(prospect);
				bestAssociation.incLife();
			} else {
				bestAssociation.setDetectedLandmark(prospect);
				bestAssociation.incLife();
			}
		}
		return newLandmarks;
	}
	
	private List<Vector2D> extractPoints(Sample sample, Robot robot) {
		List<Vector2D> points = new ArrayList<Vector2D>();
		float pointX, pointY;			
		RobotState state = robot.getEstimatedState();
		if (sample.isInfrared1MeasurementValid()) {
			pointX = (float)(state.getPosition().getX() + Math.sin(state.getHeading() + sample.getInfrared1Angle()) * sample.getInfrared1Distance());
			pointY = (float)(state.getPosition().getY() - Math.cos(state.getHeading() + sample.getInfrared1Angle()) * sample.getInfrared1Distance());
			points.add(new Vector2D(pointX, pointY));
		}
		if (sample.isInfrared2MeasurementValid()) {
			pointX = (float)(state.getPosition().getX() + Math.sin(state.getHeading() + sample.getInfrared2Angle()) * sample.getInfrared2Distance());
			pointY = (float)(state.getPosition().getY() - Math.cos(state.getHeading() + sample.getInfrared2Angle()) * sample.getInfrared2Distance());
			points.add(new Vector2D(pointX, pointY));
		}
		if (sample.isUltrasound1MeasurementValid()) {
			pointX = (float)(state.getPosition().getX() + Math.sin(state.getHeading() + sample.getUltrasound1Angle()) * sample.getUltrasound1Distance());
			pointY = (float)(state.getPosition().getY() - Math.cos(state.getHeading() + sample.getUltrasound1Angle()) * sample.getUltrasound1Distance());
			points.add(new Vector2D(pointX, pointY));
		}
		if (sample.isUltrasound2MeasurementValid()) {
			pointX = (float)(state.getPosition().getX() + Math.sin(state.getHeading() + sample.getUltrasound2Angle()) * sample.getUltrasound2Distance());
			pointY = (float)(state.getPosition().getY() - Math.cos(state.getHeading() + sample.getUltrasound2Angle()) * sample.getUltrasound2Distance());
			points.add(new Vector2D(pointX, pointY));
		}
		return points;
	}
	
}
