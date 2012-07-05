package raisa.vis;

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
	}

	public void sendStop() {
		leftSpeed = 0;
		rightSpeed = 0;
		
		communicator.sendPackage(createPackage());
	}

	public void sendBack() {
		leftSpeed = checkSpeed(leftSpeed - 1);
		rightSpeed = checkSpeed(rightSpeed - 1);
		communicator.sendPackage(createPackage());
	}

	public void sendRight() {
		leftSpeed = checkSpeed(leftSpeed + 1);
		rightSpeed = checkSpeed(rightSpeed - 1);
		communicator.sendPackage(createPackage());
	}

	public void sendLeft() {
		leftSpeed = checkSpeed(leftSpeed - 1);
		rightSpeed = checkSpeed(rightSpeed + 1);
		communicator.sendPackage(createPackage());
	}
}
