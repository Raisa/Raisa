package raisa.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import raisa.comms.SampleParser;
import raisa.util.CollectionUtil;


public class WorldModel extends Observable implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Sample> samples = new ArrayList<Sample>();
	private List<Robot> states = new ArrayList<Robot>();
	private Robot robot = new Robot();
	
	public Robot getRobot() {
		return robot;
	}
	
	public List<Sample> getSamples() {
		return samples;
	}
	
	public List<Robot> getStates() {		
		List<Robot> copy = new ArrayList<Robot>();
		synchronized(states) {
			copy.addAll(states);
		}
		return copy;
	}
	
	public void addSample(String message) {
		Sample sample = new SampleParser().parse(message);
		addSample(sample);
	}
	
	public void addSample(Sample sample) {
		robot = robot.moveRobot(sample);
		synchronized(states) {
			states.add(robot);
		}
		samples.add(sample);		
		setChanged();
		notifyObservers(sample);
	}
	
	public void reset() {
		robot = new Robot();
		samples = new ArrayList<Sample>();
		states = new ArrayList<Robot>();
	}
	
	public void removeOldSamples(int preserveLength) {
		samples = CollectionUtil.takeLast(samples, preserveLength);
	}
	
	public void clearSamples() {
		samples = new ArrayList<Sample>();		
	}
	
	public List<Sample> getLastSamples(int numberOfSamples) {
		return CollectionUtil.takeLast(samples, numberOfSamples);
	}	
}
