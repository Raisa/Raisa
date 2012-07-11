package raisa.domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.awt.Image;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import raisa.comms.SampleParser;
import raisa.util.CollectionUtil;
import raisa.util.Vector2D;


public class WorldModel extends Observable implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Sample> samples = new ArrayList<Sample>();
	private List<Robot> states = new ArrayList<Robot>();
	private Robot robot = new Robot();

	private Grid grid = new Grid();
	
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
		calculateSpeed();
		setChanged();
		notifyObservers(sample);
	}
	
	/**
	 * Calculate speed for tracks based on locations and timestamps of the previous states.
	 */
	private void calculateSpeed() {
		float currentSpeedLeftTrack = 0.0f;
		float currentSpeedRightTrack = 0.0f;
		List<Robot> pastStates = CollectionUtil.takeLast(states, 5);
		if (pastStates.size() > 1) {
			boolean isFirst = true;
			Float previousPositionLeftTrack = new Float(), previousPositionRightTrack = new Float();
			long accumulatedTime = 0, previousTimestamp = 0;
			float accumulatedDistanceLeftTrack = 0.0f, accumulatedDistanceRightTrack = 0.0f;
			for (Robot r : pastStates) {
				if (isFirst) {
					isFirst = false;
				} else {
					accumulatedDistanceLeftTrack += (r.isDirectionLeftTrackForward() ? 1.0f : -1.0f) * previousPositionLeftTrack.distance(r.getPositionLeftTrack());
					accumulatedDistanceRightTrack += (r.isDirectionRightTrackForward() ? 1.0f : -1.0f) * previousPositionRightTrack.distance(r.getPositionRightTrack());
					accumulatedTime += r.getTimestampMillis() - previousTimestamp;
				}
				previousPositionLeftTrack = r.getPositionLeftTrack();
				previousPositionRightTrack = r.getPositionRightTrack();
				previousTimestamp = r.getTimestampMillis();				
			}
			currentSpeedLeftTrack = accumulatedDistanceLeftTrack / ((float)accumulatedTime / 10.0f);
			currentSpeedRightTrack = accumulatedDistanceRightTrack / ((float)accumulatedTime / 10.0f);			
		}
		robot.setSpeedLeftTrack(currentSpeedLeftTrack);
		robot.setSpeedRightTrack(currentSpeedRightTrack);		
	}
	
	public void reset() {
		robot = new Robot();
		samples = new ArrayList<Sample>();
		states = new ArrayList<Robot>();
		grid = new Grid();
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
}
