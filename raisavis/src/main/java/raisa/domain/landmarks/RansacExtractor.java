package raisa.domain.landmarks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import raisa.util.RandomUtil;
import raisa.util.Segment2D;
import raisa.util.Vector2D;

/**
 * Adapted from "SLAM for dummies" 
 * http://ocw.mit.edu/courses/aeronautics-and-astronautics/16-412j-cognitive-robotics-spring-2005/projects/1aslam_blas_repo.pdf
 */
public class RansacExtractor {
	
	/* max times to run algorithm */
	private final static int MAX_TRIALS = 1000; 
    
	/* randomly select X points */
	private final static int MAX_SAMPLE = 2; 

	/* if less than 40 points left don't bother trying to find consensus (stop algorithm) */
	private final static int MIN_LINEPOINTS = 40;

	/* if point is within x distance of line its part of line */
	private final static float RANSAC_TOLERANCE = 1.5f; 

	/* at least votes required to determine if a line */
	private final static int RANSAC_CONSENSUS = 35;
	
	public static List<Vector2D> allPoints = new ArrayList<Vector2D>();
	
	public void reset() {
		allPoints = new ArrayList<Vector2D>();
	}
	
	public List<Landmark> extractLandmarks(List<Vector2D> dataPoints) {
	    int noTrials = 0;
	    List<Vector2D> allPoints = new ArrayList<Vector2D>(dataPoints);
	    RansacExtractor.allPoints = allPoints;
	    List<Segment2D> foundLines = new ArrayList<Segment2D>();
	    
	    while (noTrials < MAX_TRIALS && allPoints.size() > MIN_LINEPOINTS) {
	    	List<Vector2D> newAllPoints = new ArrayList<Vector2D>();
	    	List<Vector2D> consensusPoints = new ArrayList<Vector2D>();
	    	
	    	// randomly select line points and check how many remaining points fit into it
	    	List<Vector2D> rndSelectedPoints = getRandomLinePoints(allPoints);
	    	Segment2D estimatedLine = leastSquaresLineEstimate(rndSelectedPoints);
	        for (Vector2D point : allPoints) {
	        	float distance = (float) estimatedLine.ptLineDist(point);
	        	if (distance < RANSAC_TOLERANCE) {
	        		consensusPoints.add(point);
	        	} else {
	        		newAllPoints.add(point);
	        	}
	        }
	        
	        // remove points that are far from the average consensus point
	        float avgX = 0.0f;
	        float avgY = 0.0f;
	        for (Vector2D point : consensusPoints) {
	        	avgX += point.x;
	        	avgY += point.y;
	        }
	        avgX /= consensusPoints.size();
	        avgY /= consensusPoints.size();
	        
	        List<Vector2D> tmpConsensusPoints = new ArrayList<Vector2D>(consensusPoints);
	        consensusPoints.clear();
	        for (Vector2D point : tmpConsensusPoints) {
	        	if (Math.sqrt(Math.pow(avgX - point.x, 2.0f) + Math.pow(avgY - point.y, 2.0f)) < 400.0f) {
	        		consensusPoints.add(point);
	        	} else {
	        		newAllPoints.add(point);	        		
	        	}
	        }

	        // calculate new line landmark from consensus points, if enough evidence for landmark
	        if (consensusPoints.size() > RANSAC_CONSENSUS) {
	        	estimatedLine = leastSquaresLineEstimate(consensusPoints);
	        	foundLines.add(estimatedLine);
	        	allPoints = newAllPoints;
	        	noTrials = 0;
	        } else {
	        	noTrials++;
	        }      
	    }
	    
		return convertToLandmarks(foundLines);
	}
	
	private List<Landmark> convertToLandmarks(List<Segment2D> foundLines) {
		List<Landmark> landmarks = new ArrayList<Landmark>();
		for (Segment2D segment : foundLines) {
			landmarks.add(new LineLandmark(segment));
		}
		return landmarks;
	}

	private Segment2D leastSquaresLineEstimate(
			List<Vector2D> rndSelectedPoints) {
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		double[][] pointArray = new double[rndSelectedPoints.size()][2];
		for (int i=0; i<rndSelectedPoints.size(); i++) {
			Vector2D point = rndSelectedPoints.get(i);
			pointArray[i][0] = point.getX();
			pointArray[i][1] = point.getY();
			if (minX > point.getX()) {
				minX = point.getX();
			}
			if (maxX < point.getX()) {
				maxX = point.getX();
			}
			if (minY > point.getY()) {
				minY = point.getY();
			}
			if (maxY < point.getY()) {
				maxY = point.getY();
			}
		}
		SimpleRegression r = new SimpleRegression();
		r.addData(pointArray);
		return new Segment2D(
				(float)r.getSlope(), 
				(float)r.getIntercept(),
				(float)minX,
				(float)minY,
				(float)maxX,
				(float)maxY);
	}

	private List<Vector2D> getRandomLinePoints(List<Vector2D> points) {
		List<Vector2D> result = new ArrayList<Vector2D>();
		for (int i=0; i<MAX_SAMPLE; i++) {
			RandomUtil.shuffle(points);
			result.add(points.get(0));
		}
		return result;
	}

}