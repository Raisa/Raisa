package raisa.domain.landmarks;

import java.util.ArrayList;
import java.util.List;

import raisa.domain.Robot;
import raisa.domain.RobotState;
import raisa.domain.Sample;
import raisa.util.CollectionUtil;
import raisa.util.Vector2D;

public class LandmarkManager {

	private static final int RANSAC_SAMPLES = 190;
	private static final int SPIKE_SAMPLES = 50;

	private List<Landmark> landmarks = new ArrayList<Landmark>();
	
	private List<Vector2D> dataPoints = new ArrayList<Vector2D>();
	private List<Sample> samples = new ArrayList<Sample>();
	private List<Robot> states = new ArrayList<Robot>();
	private int sampleCounter = 0;
	
	private RansacExtractor ransacExtractor = new RansacExtractor();
	private SpikeExtractor spikeExtractor = new SpikeExtractor();

	public List<Landmark> getLandmarks() {
		return this.landmarks;
	}
	
	public void addData(Sample sample, Robot state) {
		if (sample == null || state == null) {
			return;
		}
		sampleCounter++;
		dataPoints.addAll(extractPoints(sample, state));
		samples.add(sample);
		states.add(state);
		if (sampleCounter % RANSAC_SAMPLES == 0) {
			landmarks.addAll(
				ransacExtractor.extractLandmarks(
					CollectionUtil.takeLast(dataPoints, 4 * RANSAC_SAMPLES)));
		} 
		if (sampleCounter % SPIKE_SAMPLES == 0) {
			landmarks.addAll(
				spikeExtractor.extractLandmarks(
					CollectionUtil.takeLast(samples, SPIKE_SAMPLES),
					CollectionUtil.takeLast(states, SPIKE_SAMPLES)));			
		}
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
