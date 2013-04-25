package raisa.comms.controller;

import raisa.comms.Communicator;
import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;
import raisa.domain.samples.SampleListener;

public class PidController extends Controller implements SampleListener {

	private WorldModel world;
	
	public PidController(WorldModel world, Communicator ... communicators) {
		this.world = world;
	}
	
	@Override
	public boolean getLights() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLeftSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRightSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPanServoAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTiltServoAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sampleAdded(Sample sample) {
		// TODO Auto-generated method stub
		
	}
//	
//	private void sendPackage() {
//		for(Communicator communicator : communicators) {
//			communicator.sendPackage(createPackage());
//		}
//	}

	@Override
	public boolean getServos() {
		// TODO Auto-generated method stub
		return false;
	}


}
