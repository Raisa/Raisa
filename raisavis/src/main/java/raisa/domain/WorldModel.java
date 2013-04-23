package raisa.domain;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import raisa.comms.SampleParser;
import raisa.comms.SensorListener;
import raisa.domain.landmarks.Landmark;
import raisa.domain.landmarks.LandmarkManager;
import raisa.domain.robot.Robot;
import raisa.domain.samples.AveragingSampleFixer;
import raisa.domain.samples.Sample;
import raisa.domain.samples.SampleFixer;
import raisa.domain.samples.SampleListener;
import raisa.util.CollectionUtil;
import raisa.util.Vector2D;


public class WorldModel implements Serializable, SensorListener {
	private static final long serialVersionUID = 1L;
	
	private List<Sample> samples = new ArrayList<Sample>();
	private List<Robot> states = new ArrayList<Robot>();
	private List<SampleFixer> sampleFixers = new ArrayList<SampleFixer>();

	private Grid grid = new Grid();
	private LandmarkManager landmarkManager = new LandmarkManager();
		
	private List<SampleListener> sampleListeners = new ArrayList<SampleListener>();
	private String latestMapFilename;
	
	public WorldModel() {
		addState(new Robot());
		sampleFixers.add(new AveragingSampleFixer(5, 40.0f));
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
	
	public List<Landmark> getLandmarks() {
		return this.landmarkManager.getLandmarks();
	}
	
	public LandmarkManager getLandmarkManager() {
		return this.landmarkManager;
	}
		
	@Override
	public synchronized void sampleReceived(String message) {		
		Sample sample = new SampleParser().parse(message);
		for (SampleFixer fixer : sampleFixers) {
		 sample = fixer.fix(sample);
		}
		addSample(sample);
	}
	
	public void addSample(Sample sample) {
		samples.add(sample);	
		notifySampleListeners(sample);
	}

	public void addState(Robot state) {
		synchronized(states) {
			states.add(state);
			Sample latestSample = getLatestSample();
		}
	}
	
	public Robot getLatestState() {
		synchronized (states) {
			if (states.size() == 0) {
				// when there are no states, the callers of this method usually fail to handle nulls properly
				// Executing Reset from menu resets state count to zero
				// so returning a null object
				return new Robot();
			}
			return states.get(states.size() - 1);
		}
	}

	public Sample getLatestSample() {
		if (samples.size() == 0) {
			return null;
		}
		return samples.get(samples.size() - 1);
	}	
	
	public void reset() {
		samples = new ArrayList<Sample>();
		states = new ArrayList<Robot>();
		for (SampleFixer fixer : sampleFixers) {
			fixer.reset();
		}
		if(latestMapFilename != null) {
			loadMap(latestMapFilename);
		} else {
			grid = new Grid();
		}
		addState(new Robot());
		landmarkManager.reset();
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

	public void setGridPosition(Vector2D position, boolean isBlocked) {
		grid.setGridPosition(position, isBlocked);
	}	
	
	public void setUserPosition(Vector2D position, boolean isBlocked) {
		grid.setUserPosition(position, isBlocked);
	}
	
	public void pushUserEditUndoLevel() {
		grid.pushUserUndoLevel();
	}
	
	public void popUserEditUndoLevel() {
		grid.popUserUndoLevel();
	}

	public void redoUserEditUndoLevel() {
		grid.redoUserUndoLevel();
	}

	public boolean isUserEditUndoable() {
		return grid.isUserEditUndoable();
	}

	public boolean isUserEditRedoable() {
		return grid.isUserEditRedoable();
	}

	public int getUserUndoLevels() {
		return grid.getUserUndoLevels();
	}

	public int getUserRedoLevels() {
		return grid.getUserRedoLevels();
	}

	public void saveMap(String fileName) {
		try {
			ImageIO.write(grid.getUserImage(), "PNG", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadMap(String fileName) {
		try {
			BufferedImage mapImage = ImageIO.read(new File(fileName));
			grid.pushUserUndoLevel();
			grid.setUserImage(mapImage);
			latestMapFilename = fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetMap() {
		grid.resetUserImage();
	}

	public Image getUserImage() {
		return grid.getUserImage();
	}

	public Image getBlockedImage() {
		return grid.getBlockedImage();
	}
	
	public float traceRay(Vector2D from, float angle) {
		return grid.traceRay(from, angle);
	}

	public float getWidth() {
		return grid.getWidth();
	}

	public float getHeight() {
		return grid.getHeight();
	}

	public void addSampleListener(SampleListener listener) {
		synchronized (sampleListeners) {
			if (this.sampleListeners.contains(listener)) {
				return;
			}
			this.sampleListeners.add(listener);
		}
	}
	
	public void removeSampleListener(SampleListener listener) {
		synchronized (sampleListeners) {
			this.sampleListeners.remove(listener);
		}
	}	
	
	private void notifySampleListeners(Sample sample) {
		synchronized (sampleListeners) {
			for (SampleListener listener : sampleListeners) {
				listener.sampleAdded(sample);
			}
		}
	}
	
	public boolean isClear(Vector2D position) {
		return grid.isClear(position);
	}
}
