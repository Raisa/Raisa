package raisa.vis;

import java.util.ArrayList;
import java.util.List;

public class BasicController {
	private byte [] speedPowerMap = new byte[] {
			(byte)0,
			(byte)160,
			(byte)175,
			(byte)195,
			(byte)220,
			(byte)255
	};

	private Communicator communicator;
	
	private int leftSpeed;
	private int rightSpeed;
	
	private List<ControlListener> controlListeners = new ArrayList<ControlListener>();
	
	public BasicController(Communicator communicator) {
		this.communicator = communicator;
	}
	
	private int checkSpeed(int speed) {
		if (speed > speedPowerMap.length - 1) {
			speed = speedPowerMap.length - 1;
		}
		if (speed < -speedPowerMap.length + 1) {
			speed = -speedPowerMap.length + 1;			
		}
		return speed;
	}

	private byte[] createPackage() {
		byte[] bytes = new byte[] {
				'R',
				'a',
				speedPowerMap[Math.abs(leftSpeed)],
				(byte)(leftSpeed >= 0 ? 'F' : 'B'),
				speedPowerMap[Math.abs(rightSpeed)],
				(byte)(rightSpeed >= 0 ? 'F' : 'B'),
				// control byte
				'i',
				's',
		};
		return bytes;
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

	private void notifyControlListeners() {
		for (ControlListener listener : controlListeners) {
			listener.controlsChanged(this);
		}
	}

	public void addContolListener(ControlListener controlListener) {
		controlListeners.add(controlListener);
	}

	public int getLeftSpeed() {
		return leftSpeed;
	}

	public int getRightSpeed() {
		return rightSpeed;
	}
}
