package raisa.comms;

import static raisa.comms.ControlMessage.SPEED_STEPS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicController extends Controller {
	private List<Communicator> communicators = new ArrayList<Communicator>();
	
	private int leftSpeed;
	private int rightSpeed;
	private boolean lights;
	private int panServoAngle = 90;
	private int tiltServoAngle = 120;
	private long sessionStartTimestamp = -1;
	
	public BasicController(Communicator ... communicators) {
		this.communicators.addAll(Arrays.asList(communicators));
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
		return new ControlMessage(leftSpeed, rightSpeed, lights, panServoAngle, tiltServoAngle);
	}

	public void sendForward() {
		leftSpeed = checkSpeed(leftSpeed + 1);
		rightSpeed = checkSpeed(rightSpeed + 1);
		
		sendPackage();
		notifyControlListeners();
	}

	public void sendStop() {
		leftSpeed = 0;
		rightSpeed = 0;
		
		sendPackage();
		notifyControlListeners();
	}

	public void sendBack() {
		leftSpeed = checkSpeed(leftSpeed - 1);
		rightSpeed = checkSpeed(rightSpeed - 1);
		sendPackage();
		notifyControlListeners();
	}

	public void sendRight() {
		leftSpeed = checkSpeed(leftSpeed + 1);
		rightSpeed = checkSpeed(rightSpeed - 1);
		sendPackage();
		notifyControlListeners();
	}

	public void sendLeft() {
		leftSpeed = checkSpeed(leftSpeed - 1);
		rightSpeed = checkSpeed(rightSpeed + 1);
		sendPackage();
		notifyControlListeners();
	}
	
	public void sendPanLeft() {
		if (panServoAngle + 5 <= 140) {
			panServoAngle += 5;
			sendPackage();
			notifyControlListeners();
		}
	}
	
	public void sendPanRight() {
		if (panServoAngle - 5 >= 40) {
			panServoAngle -= 5;
			sendPackage();
			notifyControlListeners();
		}
	}
	
	public void sendCenterPanAndTilt() {
		panServoAngle = 90;
		tiltServoAngle = 120;
		sendPackage();
		notifyControlListeners();
	}
	
	public void sendTiltDown() {
		if (tiltServoAngle + 10 <= 120) {
			tiltServoAngle += 10;
			sendPackage();
			notifyControlListeners();
		}
	}
	
	public void sendTiltUp() {
		if (tiltServoAngle - 10 >= 0) {
			tiltServoAngle -= 10;
			sendPackage();
			notifyControlListeners();
		}
	}	
	
	public void sendLights() {
		lights = !lights;
		sendPackage();
		notifyControlListeners();
	}

	private void sendPackage() {
		for(Communicator communicator :communicators) {
			communicator.sendPackage(createPackage());
		}
	}

	@Override
	public int getLeftSpeed() {
		return leftSpeed;
	}

	@Override
	public int getRightSpeed() {
		return rightSpeed;
	}

	@Override
	public boolean getLights() {
		return lights;
	}
	
	@Override
	public int getPanServoAngle() {
		return panServoAngle;
	}

	@Override
	public int getTiltServoAngle() {
		return tiltServoAngle;
	}

	public void copyListenersTo(Controller targetController) {
		for(ControllerListener controlListener : controlListeners ) {
			targetController.addContolListener(controlListener);
		}
	}
}
