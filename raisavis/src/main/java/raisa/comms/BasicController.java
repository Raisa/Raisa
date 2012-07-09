package raisa.comms;

import static raisa.comms.ControlMessage.SPEED_STEPS;

import java.util.ArrayList;
import java.util.List;

public class BasicController {
	private Communicator communicator;
	
	private int leftSpeed;
	private int rightSpeed;
	private boolean lights;
	
	private List<ControllerListener> controlListeners = new ArrayList<ControllerListener>();
	
	public BasicController(Communicator communicator) {
		this.communicator = communicator;
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
		return new ControlMessage(leftSpeed, rightSpeed, lights);
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

	private void notifyControlListeners() {
		for (ControllerListener listener : controlListeners) {
			listener.controlsChanged(this);
		}
	}

	public void addContolListener(ControllerListener controlListener) {
		controlListeners.add(controlListener);
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
}
