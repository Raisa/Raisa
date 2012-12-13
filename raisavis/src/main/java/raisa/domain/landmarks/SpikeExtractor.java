package raisa.domain.landmarks;

import java.util.ArrayList;
import java.util.List;

import raisa.domain.robot.Robot;
import raisa.domain.robot.RobotState;
import raisa.domain.samples.Sample;

public class SpikeExtractor  {

	private final static float SPIKE_THRESHOLD_3_POINTS = 75.0f;
	private final static float SPIKE_THRESHOLD_2_POINTS = 75.0f;

	public List<Landmark> extractLandmarks(List<Sample> samples, List<Robot> states) {
		List<Landmark> result = new ArrayList<Landmark>();
		for (int i=1; i<samples.size()-1; i++) {
			Sample previousSample = samples.get(i-1);
			Sample currentSample = samples.get(i);
			Sample nextSample = samples.get(i+1);
			
			if (currentSample.isInfrared1MeasurementValid()) {
				boolean spikeDetected = false;
				if (previousSample.isInfrared1MeasurementValid() && 
					nextSample.isInfrared1MeasurementValid()) {
					if (((previousSample.getInfrared1Distance()-currentSample.getInfrared1Distance()) + 
						 (nextSample.getInfrared1Distance()-currentSample.getInfrared1Distance())) > SPIKE_THRESHOLD_3_POINTS)  {
						spikeDetected = true;
					}
				}
				if (previousSample.isInfrared1MeasurementValid()) {
					if ((previousSample.getInfrared1Distance()-currentSample.getInfrared1Distance()) > SPIKE_THRESHOLD_2_POINTS) {
						spikeDetected = true;
					}
				}
				if (nextSample.isInfrared1MeasurementValid()) {
					if ((nextSample.getInfrared1Distance()-currentSample.getInfrared1Distance()) > SPIKE_THRESHOLD_2_POINTS) {
						spikeDetected = true;
					}
				}
				if (spikeDetected) {
					RobotState state = states.get(i).getEstimatedState();
					float pointX = (float)(state.getPosition().getX() + Math.sin(state.getHeading() + currentSample.getInfrared1Angle()) * currentSample.getInfrared1Distance());
					float pointY = (float)(state.getPosition().getY() - Math.cos(state.getHeading() + currentSample.getInfrared1Angle()) * currentSample.getInfrared1Distance());
					result.add(new SpikeLandmark(pointX, pointY));
				}
			}
			if (currentSample.isInfrared2MeasurementValid()) {
				boolean spikeDetected = false;
				if (previousSample.isInfrared2MeasurementValid() && 
					nextSample.isInfrared2MeasurementValid()) {
					if (((previousSample.getInfrared2Distance()-currentSample.getInfrared2Distance()) + 
						 (nextSample.getInfrared2Distance()-currentSample.getInfrared2Distance())) > SPIKE_THRESHOLD_3_POINTS)  {
						spikeDetected = true;
					}
				}
				if (previousSample.isInfrared2MeasurementValid()) {
					if ((previousSample.getInfrared2Distance()-currentSample.getInfrared2Distance()) > SPIKE_THRESHOLD_2_POINTS) {
						spikeDetected = true;
					}
				}
				if (nextSample.isInfrared2MeasurementValid()) {
					if ((nextSample.getInfrared2Distance()-currentSample.getInfrared2Distance()) > SPIKE_THRESHOLD_2_POINTS) {
						spikeDetected = true;
					}
				}
				if (spikeDetected) {
					RobotState state = states.get(i).getEstimatedState();
					float pointX = (float)(state.getPosition().getX() + Math.sin(state.getHeading() + currentSample.getInfrared2Angle()) * currentSample.getInfrared2Distance());
					float pointY = (float)(state.getPosition().getY() - Math.cos(state.getHeading() + currentSample.getInfrared2Angle()) * currentSample.getInfrared2Distance());
					result.add(new SpikeLandmark(pointX, pointY));
				}
			}
			
		}
		
		return result;
	}

}
