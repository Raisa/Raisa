package raisa.domain.landmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import raisa.domain.RobotState;
import raisa.domain.Sample;
import raisa.util.Vector2D;

/**
 * Adapted from "SLAM for dummies" 
 * http://ocw.mit.edu/courses/aeronautics-and-astronautics/16-412j-cognitive-robotics-spring-2005/projects/1aslam_blas_repo.pdf
 */
public class RansacExtractor implements LandmarkExtractor {

	/* Number of times a landmark must be observed to be recognized as a landmark */
	private final static int MIN_OBSERVATIONS = 5;
	
	/* max times to run algorithm */
	private final static int MAX_TRIALS = 100; 
    
	/* randomly select X points */
	private final static int MAX_SAMPLE = 10; 

	/* if less than 40 points left don't bother trying to find consensus (stop algorithm) */
	private final static int MIN_LINEPOINTS = 30;

	/* if point is within x distance of line its part of line */
	private final static float RANSAC_TOLERANCE = 8.0f; 

	/* at least 30 votes required to determine if a line */
	private final static int RANSAC_CONSENSUS = 30;
	
	@Override
	public List<Landmark> extractLandmarks(List<Sample> samples,
			List<RobotState> states) {
	    int noTrials = 0;
	    List<Vector2D> allPoints = extractPoints(samples, states);
	    List<LineEquation> foundLines = new ArrayList<LineEquation>();
	    
	    while (noTrials < MAX_TRIALS && allPoints.size() > MIN_LINEPOINTS) {
	    	List<Vector2D> newAllPoints = new ArrayList<Vector2D>();
	    	List<Vector2D> consensusPoints = new ArrayList<Vector2D>();
	    	
	    	// randomly select line points and check how many remaining points fit into it
	    	List<Vector2D> rndSelectedPoints = getRandomLinePoints(allPoints);
	        LineEquation estimatedLine = leastSquaresLineEstimate(rndSelectedPoints);
	        for (Vector2D point : allPoints) {
	        	float distance = distanceToLine(point, estimatedLine);
	        	if (distance < RANSAC_TOLERANCE) {
	        		consensusPoints.add(point);
	        	} else {
	        		newAllPoints.add(point);
	        	}
	        }
	        
	        // calculate new line landmark from consensus points, if enought evidence for landmark
	        if (consensusPoints.size() > RANSAC_CONSENSUS) {
	        	estimatedLine = leastSquaresLineEstimate(consensusPoints);
	        	allPoints = newAllPoints;
	        	foundLines.add(estimatedLine);
	        	noTrials = 0;
	        } else {
	        	noTrials++;
	        }      
	    }
	    
		return convertToLandmarks(foundLines);
	}
	
	private List<Landmark> convertToLandmarks(List<LineEquation> foundLines) {
		// TODO Auto-generated method stub
		return null;
	}

	private float distanceToLine(Vector2D linePoint, LineEquation estimatedLine) {
		// TODO Auto-generated method stub
		return 0;
	}

	private LineEquation leastSquaresLineEstimate(
			List<Vector2D> rndSelectedPoints) {
		double[][] pointArray = new double[rndSelectedPoints.size()][2];
		for (int i=0; i<rndSelectedPoints.size(); i++) {
			Vector2D point = rndSelectedPoints.get(i);
			pointArray[i][0] = point.getX();
			pointArray[i][1] = point.getY();
		}
		SimpleRegression r = new SimpleRegression();
		r.addData(pointArray);
		return new LineEquation((float)r.getSlope(), (float)r.getIntercept());
	}

	private List<Vector2D> getRandomLinePoints(List<Vector2D> points) {
		List<Vector2D> result = new ArrayList<Vector2D>();
		for (int i=0; i<MAX_SAMPLE; i++) {
			Collections.shuffle(points);
			result.add(points.get(0));
		}
		return result;
	}

	private List<Vector2D> extractPoints(List<Sample> samples,
			List<RobotState> states) {
		List<Vector2D> points = new ArrayList<Vector2D>();
		float pointX, pointY;
		for (int i=0; i<samples.size(); i++) {
			Sample sample = samples.get(i);
			RobotState state = states.get(i);
			if (sample.isInfrared1MeasurementValid()) {
				pointX = (float)(state.getPosition().getX() + Math.cos(state.getHeading() + sample.getInfrared1Angle()) * sample.getInfrared1Distance());
				pointY = (float)(state.getPosition().getY() + Math.sin(state.getHeading() + sample.getInfrared1Angle()) * sample.getInfrared1Distance());
				points.add(new Vector2D(pointX, pointY));
			}
			if (sample.isInfrared2MeasurementValid()) {
				pointX = (float)(state.getPosition().getX() + Math.cos(state.getHeading() + sample.getInfrared2Angle()) * sample.getInfrared2Distance());
				pointY = (float)(state.getPosition().getY() + Math.sin(state.getHeading() + sample.getInfrared2Angle()) * sample.getInfrared2Distance());
				points.add(new Vector2D(pointX, pointY));
			}
		}
		return points;
	}

}