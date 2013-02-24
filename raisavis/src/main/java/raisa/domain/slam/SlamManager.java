package raisa.domain.slam;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import raisa.domain.landmarks.Landmark;
import raisa.domain.robot.RobotState;
import raisa.util.Vector2D;

public class SlamManager {

	private RobotState previousState;
	private RealMatrix I2, I3, sigma;
	private NormalDistribution odometryNoise, headingNoise, sensorRangeNoise, sensorDirectionNoise;
	private RealVector X;
	private int slamIdSeq = 0;
	
	public SlamManager() {
		X = new ArrayRealVector(new double[] { 0.0d, 0.0d, -Math.PI / 2.0d });
		I2 = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d }, { 0.0d, 1.0d }});		
		I3 = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d, 0.0d }, { 0.0d, 1.0d, 0.0d }, { 0.0d, 0.0d, 1.0d } });		
		sigma = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d, 0.0d }, { 0.0d, 1.0d, 0.0d }, { 0.0d, 1.0d, 0.0d } });		
		odometryNoise = new NormalDistribution(0.0d, 1.0d);
		headingNoise = new NormalDistribution(0.0d, 0.2d);
		sensorRangeNoise = new NormalDistribution(0.0d, 1.0d);
		sensorDirectionNoise = new NormalDistribution(0.0d, 0.3d);
		
		previousState = new RobotState();
	}
	
	public synchronized RobotState update(
			RobotState estimatedState,
			List<Landmark> landmarks) {
		
		Vector2D previousPosition = previousState.getPosition();    // previous SLAM estimate
		Vector2D estimatedPosition = estimatedState.getPosition();  // current odometry estimate
		
		// phase 1. update current state using the odometry data

		RealMatrix Fx = new Array2DRowRealMatrix(3, 3 + 2 * slamIdSeq);
		Fx.setSubMatrix(I3.getData(), 0, 0);
		
		double deltaX = estimatedPosition.x - previousPosition.x;
		double deltaY = estimatedPosition.y - previousPosition.y;
		double deltaT = calculateDifferenceBetweenAngles(previousState.getHeading(), estimatedState.getHeading());

		RealVector uBar = X.copy();
		uBar.addToEntry(0, deltaX);
		uBar.addToEntry(1, deltaY);
		uBar.addToEntry(2, deltaT);
		
		RealMatrix A = new Array2DRowRealMatrix(
				new double[][] 
						{ { 0.0d, 0.0d, -deltaY }, 
						{ 0.0d, 0.0d, deltaX }, 
						{ 0.0d, 0.0d, 0.0d } });
		RealMatrix Gt = MatrixUtils.createRealIdentityMatrix(Fx.getColumnDimension()).add((Fx.transpose()).multiply(A).multiply(Fx));
		
		RealVector W = new ArrayRealVector(new double[] { deltaX, deltaY, deltaT });
		RealMatrix Rx = W.outerProduct(W);
		Rx.multiplyEntry(0, 0, odometryNoise.sample());
		Rx.multiplyEntry(1, 1, odometryNoise.sample());
		Rx.multiplyEntry(2, 2, headingNoise.sample());
		
		RealMatrix sigmaBar = Gt.multiply(sigma).multiply(Gt.transpose());
		sigmaBar = sigmaBar.add((Fx.transpose()).multiply(Rx.multiply(Fx)));
		
		RealMatrix Qt = new Array2DRowRealMatrix(
				new double[][] { 
						{ 16.0d, // Math.pow(sensorRangeNoise.sample(), 2.0d), 
							0.0d }, 
						{ 0.0d, 
							0.1d }}); //Math.pow(sensorDirectionNoise.sample(), 2.0d) }}); 	
				
		// Step 2: Update state from re-observed landmarks
		
		for (Landmark landmark : landmarks) {
			Integer slamId = landmark.getSlamId();
			Vector2D landmarkPosition = landmark.getPosition();
			
			if (!landmark.isTrusted()) {
				continue;
			}
			
			if (slamId == null) {
				RealVector landmarkPositionVector = new ArrayRealVector(
						new double[] { landmarkPosition.getX(), landmarkPosition.getY() });
				uBar = uBar.append(landmarkPositionVector);
				landmark.setSlamId(slamIdSeq++);

				RealMatrix sigmaTmp = sigmaBar.createMatrix(sigmaBar.getRowDimension() + 2, sigmaBar.getColumnDimension() + 2);
				sigmaTmp.setSubMatrix(sigmaBar.getData(), 0, 0);
				sigmaTmp.setSubMatrix(I2.scalarMultiply(1.0d).getData(), sigmaBar.getRowDimension() - 2, sigmaBar.getColumnDimension() - 2);
				sigmaBar = sigmaTmp;
			}
			
			// range and direction to recorded landmark
			RealVector d1 = new ArrayRealVector(
					new double[] { 
							uBar.getEntry(3 + 2 * landmark.getSlamId()) - uBar.getEntry(0), 
							uBar.getEntry(3 + 2 * landmark.getSlamId() + 1) - uBar.getEntry(1)
					});
			double q1 = d1.dotProduct(d1);
			RealVector zHat = new ArrayRealVector(
					new double[] { 
							Math.sqrt(q1),
							Math.atan2(d1.getEntry(1), d1.getEntry(0)) - uBar.getEntry(2)
					});
			
			// range and direction to observed landmark
			Vector2D detectedPosition;
			if (landmark.getDetectedLandmark()==null) {
				detectedPosition = landmark.getPosition();
			} else {
				detectedPosition = landmark.getDetectedLandmark().getPosition();
			}
			RealVector d2 = new ArrayRealVector(
					new double[] { 
							detectedPosition.getX() - uBar.getEntry(0), 
							detectedPosition.getY() - uBar.getEntry(1)
					});
			double q2 = d2.dotProduct(d2);
			RealVector z = new ArrayRealVector(
					new double[] { 
							Math.sqrt(q2),
							Math.atan2(d2.getEntry(1), d2.getEntry(0)) - uBar.getEntry(2)
					});
						
			RealMatrix Fxj = new Array2DRowRealMatrix(5, sigmaBar.getColumnDimension());
			Fxj.setSubMatrix(I3.getData(), 0, 0);
			Fxj.setSubMatrix(I2.getData(), 3, 3 + 2 * landmark.getSlamId());
			
			RealMatrix Hit = new Array2DRowRealMatrix(
					new double[][] {
							{ -Math.sqrt(q1) * d1.getEntry(0), -Math.sqrt(q1) * d1.getEntry(1), 0, +Math.sqrt(q1) * d1.getEntry(0), +Math.sqrt(q1) * d1.getEntry(1) },
							{ d1.getEntry(1), -d1.getEntry(0), -q1, -d1.getEntry(1), d1.getEntry(0) }
					});
			Hit = (Hit.scalarMultiply(1/q1)).multiply(Fxj);
			printMatrix(Hit, "Hit");
						
			RealMatrix tmp = Hit.multiply(sigmaBar).multiply(Hit.transpose());
			tmp = tmp.add(Qt);
			DecompositionSolver solver = new LUDecomposition(tmp).getSolver();
			RealMatrix Inno = solver.getInverse();

			RealMatrix Kit = sigmaBar.multiply(Hit.transpose()).multiply(Inno);
			printMatrix(Kit, "Kit");
			uBar = uBar.add(Kit.operate(z.subtract(zHat)));
			sigmaBar = (MatrixUtils.createRealIdentityMatrix(sigmaBar.getColumnDimension()).subtract((Kit.multiply(Hit)))).multiply(sigmaBar);
		}
		printMatrix(sigmaBar, "sigma");
		System.out.println("uBar:"+uBar);
		
		X = uBar;
		sigma = sigmaBar;
		
		previousState = new RobotState(new Vector2D((float)X.getEntry(0), (float)X.getEntry(1)), (float)this.polarAngleToHeading(X.getEntry(2)));	

		return previousState;
	}
	
	private void printMatrix(RealMatrix matrix, String name) {
		DecimalFormat f = new DecimalFormat("###.###");
		System.out.println(name);
		double[][] P_data = matrix.getData();
		for (int i = 0; i<P_data.length; i++) {
			for (int j = 0; j<P_data[i].length; j++) {
				System.out.print(f.format(P_data[i][j]) + ",");
			}
			System.out.println();
		}
	}
	
	private double calculateDifferenceBetweenAngles(double firstAngle, double secondAngle) {
		double difference = secondAngle - firstAngle;
		if (difference < -Math.PI) 
			return 2 * Math.PI + difference;
		if (difference > Math.PI) 
	        return - 2 * Math.PI + difference;
		return difference;
	 }

	private double headingToPolarAngle(double heading) {
		if (heading < 3 * Math.PI / 2.0d) {
			return heading - Math.PI / 2.0d;
		} else {
			return -Math.PI / 2.0 + (-2 * Math.PI + heading);
		}
	}
	
	private double polarAngleToHeading(double angle) {
		double tmp = angle % (2 * Math.PI);
		if (tmp < 0.0d) {
			tmp = 2 * Math.PI + angle;
		}
		return (Math.PI / 2.0d + tmp) % (2 * Math.PI);
	}	
	
	public static void main(String[] arg) {
		SlamManager test = new SlamManager();
		System.out.println(test.calculateDifferenceBetweenAngles(0.1d, 2.0d));
		System.out.println(test.calculateDifferenceBetweenAngles(0.1d, 5.0d));
		System.out.println(test.calculateDifferenceBetweenAngles(0.1d, 3.0d));
		System.out.println(test.calculateDifferenceBetweenAngles(5.0d, 0.1d));
		System.out.println(test.polarAngleToHeading(0.1d));
		System.out.println(test.polarAngleToHeading(5.0d));
		System.out.println(test.polarAngleToHeading(2.0d));
		
	}
	
}
