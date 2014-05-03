package raisa.comms;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

import java.util.Arrays;

import com.google.gson.Gson;

public class ControlMessage {
	private static final int[] speedPowerMap = new int[] { 0, 100, 115, 135, 160, 195 };
	private static int speedPowerCorrection = 4;

	public static final int SPEED_STEPS = speedPowerMap.length;

	private static int idSequence = 0;

	private final int id;
	private final int leftSpeed;
	private final int rightSpeed;
	private final int panServoAngle;
	private final int tiltServoAngle;
	private final boolean lights;
	private final boolean takePicture;
	private final boolean servos;
	private final boolean rawValues;

	private long timestamp;

	public ControlMessage(int leftSpeed, int rightSpeed,
			boolean lights, int panServoAngle, int tiltServoAngle,
			boolean takePicture, boolean servos, boolean rawValues) {
		this.id = getNextId();
		this.leftSpeed = leftSpeed;
		this.rightSpeed = rightSpeed;
		this.lights = lights;
		this.panServoAngle = panServoAngle;
		this.tiltServoAngle = tiltServoAngle;
		this.takePicture = takePicture;
		this.servos = servos;
		this.rawValues = rawValues;
	}

	private synchronized static int getNextId() {
		return idSequence++;
	}

	public static int[] getSpeedPowerMap() {
		return Arrays.copyOf(speedPowerMap, speedPowerMap.length);
	}

	public static ControlMessage fromJson(String json) {
		return new Gson().fromJson(json, ControlMessage.class);
	}

	public byte[] toSerialMessage() {
		byte[] bytes = new byte[] { 'R', 'a',
				getLeftMotorControl(),
				(byte) (leftSpeed >= 0 ? 'F' : 'B'),
				getRightMotorControl(),
				(byte) (rightSpeed >= 0 ? 'F' : 'B'),
				(byte) (panServoAngle & 0xFF),
				(byte) (tiltServoAngle & 0xFF),
				(byte) ((lights ? 2 : 1) | (takePicture ? 4 : 0) | (servos ? 0 : 8)),
				(byte) (id % 10),
				'i', 's', };
		return bytes;
	}

	private byte getLeftMotorControl() {
		int result;
		if (rawValues) {
			result = Math.abs(leftSpeed);
		} else {
			result = speedPowerMap[Math.abs(leftSpeed)];
		}
		if (result != 0) {
			result -= speedPowerCorrection;
		}
		return (byte)result;
	}

	private byte getRightMotorControl() {
		int result;
		if (rawValues) {
			result = Math.abs(rightSpeed);
		} else {
			result = speedPowerMap[Math.abs(rightSpeed)];
		}
		if (result != 0) {
			result += speedPowerCorrection;
		}
		return (byte)result;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return String.format("id:%s,time:%s,left:%s,right:%s,lights:%s,panangle:%s,tiltangle:%s,picture:%s,servos:%s",
				id, getTimestamp(), leftSpeed, rightSpeed, lights, panServoAngle, tiltServoAngle, takePicture, servos);
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

	public int getId() {
		return id;
	}

	public boolean isServos() {
		return servos;
	}

	public boolean isLights() {
		return lights;
	}

	public boolean isRawValues() {
		return rawValues;
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
