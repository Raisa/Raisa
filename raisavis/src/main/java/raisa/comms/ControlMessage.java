package raisa.comms;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

import com.google.gson.Gson;

public class ControlMessage {
	private static final byte[] speedPowerMap = new byte[] { (byte) 0,
			(byte) 160, (byte) 175, (byte) 195, (byte) 220, (byte) 255 };

	public static final int SPEED_STEPS = speedPowerMap.length;

	private final int leftSpeed;
	private final int rightSpeed;
	private final int panServoAngle;
	private final int tiltServoAngle;
	private final boolean lights;

	private long timestamp;

	public ControlMessage(int leftSpeed, int rightSpeed,
			boolean lights, int panServoAngle, int tiltServoAngle) {
		this.leftSpeed = leftSpeed;
		this.rightSpeed = rightSpeed;
		this.lights = lights;
		this.panServoAngle = panServoAngle;
		this.tiltServoAngle = tiltServoAngle;
	}

	public static ControlMessage fromJson(String json) {
		return new Gson().fromJson(json, ControlMessage.class);
	}

	public byte[] toSerialMessage() {
		byte[] bytes = new byte[] { 'R', 'a',
				speedPowerMap[Math.abs(leftSpeed)],
				(byte) (leftSpeed >= 0 ? 'F' : 'B'),
				speedPowerMap[Math.abs(rightSpeed)],
				(byte) (rightSpeed >= 0 ? 'F' : 'B'), 
				(byte) (panServoAngle & 0xFF),
				(byte) (tiltServoAngle & 0xFF),
				(byte) (lights ? 2 : 1),
				'i', 's', };
		return bytes;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return String.format("time:%s,left:%s,right:%s,lights:%s",
				getTimestamp(), leftSpeed, rightSpeed, lights);
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

	public boolean isLights() {
		return lights;
	}
	
	public int getLeftSpeed() {
		return leftSpeed;
	}

	public int getRightSpeed() {
		return rightSpeed;
	}
	
	public int getPanServoAngle() {
		return panServoAngle;
	}	

	public int getTiltServoAngle() {
		return tiltServoAngle;
	}	

	@Override
	public boolean equals(Object obj) {
		return reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return reflectionHashCode(this);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
