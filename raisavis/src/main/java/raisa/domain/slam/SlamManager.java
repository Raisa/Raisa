package raisa.domain.slam;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import raisa.domain.landmarks.Landmark;
import raisa.domain.robot.RobotState;
import raisa.util.Vector2D;

public class SlamManager {

	private RobotState previousState;
	private RealMatrix A, Q, P, H, R, Jxr, Jz;
	private RealVector X, W;
	private double CONST_C = 0.02;
	private int slamIdSeq = 0;
	
	public SlamManager() {
		X = new ArrayRealVector(new double[] { 0.0d, 0.0d, 0.0d });
		A = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d, 0.0d }, { 0.0d, 1.0d, 0.0d }, { 0.0d, 0.0d, 1.0d } });
		P = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d, 0.0d }, { 0.0d, 1.0d, 0.0d }, { 0.0d, 0.0d, 1.0d } });
		R = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d }, { 0.0d, 1.0d } });
		previousState = new RobotState();
	}
	
	public synchronized RobotState update(
			RobotState estimatedState,
			List<Landmark> landmarks) {
		int slamLandmarksCount = 0;		
		
		Vector2D previousPosition = previousState.getPosition();
		Vector2D estimatedPosition = estimatedState.getPosition();
		
		// phase 1. update current state using the odometry data
		
		double deltaX = estimatedPosition.x - previousPosition.x;
		double deltaY = estimatedPosition.y - previousPosition.y;
		double deltaT = estimatedState.getHeading() - previousState.getHeading();
		double deltaD = Math.sqrt(Math.pow(deltaX, 2.0d) + Math.sqrt(Math.pow(deltaY, 2.0d)));

		X.addToEntry(0, deltaX);
		X.addToEntry(1, deltaY);
		X.addToEntry(2, deltaT);
		
		A.setEntry(0, 2, -deltaY);
		A.setEntry(1, 2, deltaX);

		W = new ArrayRealVector(new double[] { deltaX, deltaY, deltaT });
		Q = W.outerProduct(W).scalarMultiply(CONST_C);
		
		RealMatrix Prr = P.getSubMatrix(0, 2, 0, 2);
		Prr = A.multiply(Prr).multiply(A).add(Q);
		P.setSubMatrix(Prr.getData(), 0, 0);
		
		for (Landmark landmark : landmarks) {
			Integer slamId = landmark.getSlamId();
			if (slamId != null) {
				RealMatrix Pri = P.getSubMatrix(0, 2, 2 * (slamId) + 3, 2 * (slamId) + 4);
				Pri = A.multiply(Pri);
				P.setSubMatrix(Pri.getData(), 0, 2 * (slamId) + 3);
				slamLandmarksCount++;
			}
		}
		
		// Step 2: Update state from re-observed landmarks
		
		for (Landmark landmark : landmarks) {
			Integer slamId = landmark.getSlamId();
			if (slamId != null && landmark.getDetectedLandmark() != null) {
				H = new Array2DRowRealMatrix(2, 2 * slamLandmarksCount + 3);  // creates zero matrix?
				double lambdaX = landmark.getPosition().x;
				double lambdaY = landmark.getPosition().y;
				double r = Math.sqrt(Math.pow(lambdaX - estimatedPosition.x, 2.0d) + Math.pow(lambdaY - estimatedPosition.y, 2.0d));
				double r2 = r * r;
				double a = (estimatedPosition.x - lambdaX) / r;
				double b = (estimatedPosition.y - lambdaY) / r;
				double c = 0.0d;
				double d = (lambdaY - estimatedPosition.y) / r2;
				double e = (lambdaX - estimatedPosition.x) / r2;
				double f = -1.0d;
				
				H.setEntry(0, 0, a);
				H.setEntry(0, 1, b);
				H.setEntry(0, 2, c);
				H.setEntry(1, 0, d);
				H.setEntry(1, 1, e);
				H.setEntry(1, 2, f);

				H.setEntry(0, 2 * slamId + 3, -a);
				H.setEntry(0, 2 * slamId + 3, -b);
				H.setEntry(1, 2 * slamId + 4, -d);
				H.setEntry(1, 2 * slamId + 4, -e);
				
				// K=P*HT *(H*P*HT +V*R*VT)-1
				RealMatrix K = H.multiply(P).multiply(H.transpose()).add(R);
				K = new LUDecomposition(K).getSolver().getInverse();
				K = P.multiply(H.transpose()).multiply(K);
				
				double estimatedRangeToLandmark = r;
				double estimatedDirectionToLandmark = Math.atan((lambdaY - estimatedPosition.y) / (lambdaX - estimatedPosition.x)) - this.previousState.getHeading();
				RealVector h = new ArrayRealVector(new double[] { estimatedRangeToLandmark, estimatedDirectionToLandmark });
				
				Vector2D detectedPosition = landmark.getDetectedLandmark().getPosition();
				estimatedRangeToLandmark = Math.sqrt(Math.pow(detectedPosition.x - estimatedPosition.x, 2.0d) + Math.pow(detectedPosition.y - estimatedPosition.y, 2.0d));
				estimatedDirectionToLandmark = Math.atan((detectedPosition.y - estimatedPosition.y) / (detectedPosition.x - estimatedPosition.x)) - this.previousState.getHeading();
				RealVector z = new ArrayRealVector(new double[] { estimatedRangeToLandmark, estimatedDirectionToLandmark });

				X = X.add(K.operate(z.subtract(h)));
				
				landmark.setDetectedLandmark(null);
			}
		}
		
		// Step 3: Add new landmarks to the current state
		
		Jxr = new Array2DRowRealMatrix(new double[][] { { 1.0d, 0.0d, -deltaY }, { 0.0d, 1.0d, deltaX } });
		Jz = new Array2DRowRealMatrix(new double[][] { 
				{ Math.cos(previousState.getHeading() + deltaT), - deltaD * Math.sin(previousState.getHeading() + deltaT) },
				{ Math.sin(previousState.getHeading() + deltaT), deltaD * Math.cos(previousState.getHeading() + deltaT) } });
		for (Landmark landmark : landmarks) {
			Vector2D landmarkPosition = landmark.getPosition();
			if (landmark.getSlamId() == null && landmark.isTrusted()) {
				landmark.setSlamId(slamIdSeq++);
				RealVector X_tmp = new ArrayRealVector(new double[] { landmarkPosition.x, landmarkPosition.y }); 
				X = X.append(X_tmp);
				
				// set landmark covariance
				RealMatrix Ppos = new Array2DRowRealMatrix(new double[][] 
						{ { X.getEntry(0), 0.0d, 0.0d }, 
						{ 0.0d, X.getEntry(1), 0.0d }, 
						{ 0.0d, 0.0d, X.getEntry(2) } });
				RealMatrix Pn1 = Jxr.multiply(Ppos).multiply(Jxr.transpose());
				RealMatrix Pn2 = Jz.multiply(R).multiply(Jz.transpose());
				RealMatrix Pnew = P.createMatrix(P.getRowDimension() + 2, P.getColumnDimension() + 2);
				Pnew.setSubMatrix(P.getData(), 0, 0);
				P = Pnew;
				P.setSubMatrix(Pn1.add(Pn2).getData(), P.getRowDimension() - 3, P.getColumnDimension() - 3);
				
				// set robot-landmark and landmark-robot covariances
				RealMatrix PrN1 = Prr.multiply(Jxr.transpose());
				P.setSubMatrix(PrN1.getData(), 0, P.getColumnDimension() - 3);
				P.setSubMatrix(PrN1.transpose().getData(), P.getRowDimension() - 3, 0);
				
				// landmark-landmark covariances
				for (int i = 3; i < P.getColumnDimension(); i = i + 2) {
					RealMatrix tmp = Jxr.multiply(P.getSubMatrix(0, 2, i, i+1));
					P.setSubMatrix(tmp.getData(), P.getRowDimension() - 3, i);
					P.setSubMatrix(tmp.transpose().getData(), i, P.getColumnDimension() - 3);
				}
			}
		}
			
		previousState = new RobotState(new Vector2D((float)X.getEntry(0), (float)X.getEntry(1)), (float)X.getEntry(2));	
		return previousState;
	}
	
}
