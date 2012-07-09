package raisa.comms;

public class ControlMessage {
	private static final byte [] speedPowerMap = new byte[] {
		(byte)0,
		(byte)160,
		(byte)175,
		(byte)195,
		(byte)220,
		(byte)255
	};

	public static final int SPEED_STEPS = speedPowerMap.length; 
	private static long firstMessageTimestamp = -1;
	
	private final int leftSpeed;
	private final int rightSpeed;
	private final boolean lights;
	private final long timestamp;
	public ControlMessage(int leftSpeed, int rightSpeed, boolean lights) {
		this.leftSpeed = leftSpeed;
		this.rightSpeed = rightSpeed;
		this.lights = lights;
		this.timestamp = System.currentTimeMillis();
		if(firstMessageTimestamp < 0) {
			firstMessageTimestamp = timestamp;
		}
	}
	
	public byte[] toSerialMessage() {
		byte[] bytes = new byte[] {
				'R',
				'a',
				speedPowerMap[Math.abs(leftSpeed)],
				(byte)(leftSpeed >= 0 ? 'F' : 'B'),
				speedPowerMap[Math.abs(rightSpeed)],
				(byte)(rightSpeed >= 0 ? 'F' : 'B'),
				(byte)(lights ? 2 : 1),
				'i',
				's',
		};
		return bytes;
	}
	
	public long getTimestamp() {
		return timestamp - firstMessageTimestamp;
	}
	
	@Override
	public String toString() {
		return String.format("time:%s,left:%s,right:%s,lights:%s", getTimestamp(), leftSpeed, rightSpeed, lights);
	}
}
