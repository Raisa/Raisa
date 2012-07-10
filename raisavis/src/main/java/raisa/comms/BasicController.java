package raisa.comms;

import static raisa.comms.ControlMessage.SPEED_STEPS;

public class BasicController extends Controller {
	private Communicator communicator;
	
	private int leftSpeed;
	private int rightSpeed;
	private boolean lights;
	private long sessionStartTimestamp = -1;
	
	public BasicController(Communicator communicator) {
		this.communicator = communicator;
	}

	public void resetSession() {
		this.sessionStartTimestamp = System.currentTimeMillis();
	}
	
	private int checkSpeed(int speed) {
		if (speed > SPEED_STEPS - 1) {
			speed = SPEED_STEPS - 1;
		}
		if (speed < -SPEED_STEPS + 1) {
			speed = -SPEED_STEPS + 1;			
		}
		return speed;
	}

	private ControlMessage createPackage() {
		if(sessionStartTimestamp  < 0) {
			resetSession();
		}
		long relativeTimestamp = System.currentTimeMillis() - sessionStartTimestamp;
		return new ControlMessage(relativeTimestamp, leftSpeed, rightSpeed, lights);
	}

	public void sendForward() {
		leftSpeed = checkSpeed(leftSpeed + 1);
		rightSpeed = checkSpeed(rightSpeed + 1);
		
		communicator.sendPackage(createPackage());
		notifyControlListeners();
	}

	public void sendStop() {
		leftSpeed = 0;
		rightSpeed = 0;
		
		communicator.sendPackage(createPackage());
		notifyControlListeners();
	}

	public void sendBack() {
		leftSpeed = checkSpeed(leftSpeed - 1);
		rightSpeed = checkSpeed(rightSpeed - 1);
		communicator.sendPackage(createPackage());
		notifyControlListeners();
	}

	public void sendRight() {
		leftSpeed = checkSpeed(leftSpeed + 1);
		rightSpeed = checkSpeed(rightSpeed - 1);
		communicator.sendPackage(createPackage());
		notifyControlListeners();
	}

	public void sendLeft() {
		leftSpeed = checkSpeed(leftSpeed - 1);
		rightSpeed = checkSpeed(rightSpeed + 1);
		communicator.sendPackage(createPackage());
		notifyControlListeners();
	}
	
	public void sendLights() {
		lights = !lights;
		communicator.sendPackage(createPackage());
		notifyControlListeners();
	}

	public int getLeftSpeed() {
		return leftSpeed;
	}

	public int getRightSpeed() {
		return rightSpeed;
	}

	public boolean getLights() {
		return lights;
	}

	public void copyListenersTo(Controller targetController) {
		for(ControllerListener controlListener : controlListeners ) {
			targetController.addContolListener(controlListener);
		}
	}
}
