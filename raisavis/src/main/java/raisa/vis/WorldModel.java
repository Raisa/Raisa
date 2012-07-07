package raisa.vis;

import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class WorldModel extends Observable {
	private static final long serialVersionUID = 1L;

	private List<Sample> samples = new ArrayList<Sample>();
	private Robot robot = new Robot();
	
	public Robot getRobot() {
		return robot;
	}
	
	public List<Sample> getSamples() {
		return samples;
	}
	
	public void addSample(String message) {
		Sample sample = new Sample(robot.position.x, robot.position.y, robot.heading, message);
		addSample(sample);
	}
	
	public void addSample(Sample sample) {
		samples.add(sample);
		robot.position.x = sample.getX();
		robot.position.y = sample.getY();
		robot.heading = sample.getHeading();
		this.setChanged();
		this.notifyObservers(sample);
	}
	
	public void reset() {
		robot.heading = 0.0f;
		robot.position = new Float();
		samples = new ArrayList<Sample>();
	}
	
	public void removeOldSamples(int preserveLength) {
		samples = takeLast(samples, preserveLength);
	}
	
	public void clearSamples() {
		samples = new ArrayList<Sample>();		
	}
	
	public static List<Sample> takeLast(List<Sample> samples, int length) {
		if (samples.size() > length) {
			int fromIndex = Math.max(0, samples.size() - length);
			int toIndex = samples.size();
			List<Sample> newSamples = new ArrayList<Sample>();
			newSamples.addAll(samples.subList(fromIndex, toIndex));
			return newSamples;
		}
		return samples;
	}	
	
}
