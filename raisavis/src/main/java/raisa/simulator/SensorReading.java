package raisa.simulator;

import raisa.util.Vector3D;

public class SensorReading {
	private Long timestamp;
	private Integer messageNumber;
	private Vector3D gyro;
	private Vector3D acceleration;
	private Integer irDistance;
	private Integer sonarDistance;
	private Integer irDirection;
	private Integer sonarDirection;
	private Integer compassHeading;
	private Integer leftEncoder;
	private Integer rightEncoder;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("STA;");
		addField(builder, "NO", messageNumber);
		addField(builder, "TI", timestamp);
		addField(builder, "RR", rightEncoder);
		addField(builder, "RL", leftEncoder);
		addField(builder, "Sd", sonarDistance);
		addField(builder, "Id", irDistance);
		addField(builder, "SR", sonarDirection);
		addField(builder, "IR", irDirection);
		addField(builder, "CD", compassHeading);
		if (gyro != null) {
			addField(builder, "Gx", gyro.getX());
			addField(builder, "Gy", gyro.getY());
			addField(builder, "Gz", gyro.getZ());
		}
		if (acceleration != null) {
			addField(builder, "Ax", acceleration.getX());
			addField(builder, "Ay", acceleration.getY());
			addField(builder, "Az", acceleration.getZ());
		}
		builder.append("END;");
		return builder.toString();
	}

	private StringBuilder addField(StringBuilder builder, String key, Object value) {
		if (value == null) {
			return builder;
		}
		builder.append(key);
		builder.append(value.toString());
		builder.append(";");
		return builder;
	}
	private StringBuilder addField(StringBuilder builder, String key, Float value) {
		if (value == null) {
			return builder;
		}
		builder.append(key);
		builder.append(String.format("%f", value));
		builder.append(";");
		return builder;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public SensorReading setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public Integer getMessageNumber() {
		return messageNumber;
	}

	public SensorReading setMessageNumber(Integer messageNumber) {
		this.messageNumber = messageNumber;
		return this;
	}

	public Vector3D getGyro() {
		return gyro;
	}

	public SensorReading setGyro(Vector3D gyro) {
		this.gyro = gyro;
		return this;
	}

	public Vector3D getAcceleration() {
		return acceleration;
	}

	public SensorReading setAcceleration(Vector3D acceleration) {
		this.acceleration = acceleration;
		return this;
	}

	public Integer getIrDistance() {
		return irDistance;
	}

	public SensorReading setIrDistance(Integer irDistance) {
		this.irDistance = irDistance;
		return this;
	}

	public Integer getSonarDistance() {
		return sonarDistance;
	}

	public SensorReading setSonarDistance(Integer sonarDistance) {
		this.sonarDistance = sonarDistance;
		return this;
	}

	public Integer getIrDirection() {
		return irDirection;
	}

	public SensorReading setIrDirection(Integer irDirection) {
		this.irDirection = irDirection;
		return this;
	}

	public Integer getSonarDirection() {
		return sonarDirection;
	}

	public SensorReading setSonarDirection(Integer sonarDirection) {
		this.sonarDirection = sonarDirection;
		return this;
	}

	public Integer getCompassHeading() {
		return compassHeading;
	}

	public SensorReading setCompassHeading(Integer compassHeading) {
		this.compassHeading = compassHeading;
		return this;
	}

	public Integer getLeftEncoder() {
		return leftEncoder;
	}

	public SensorReading setLeftEncoder(Integer leftEncoder) {
		this.leftEncoder = leftEncoder;
		return this;
	}

	public Integer getRightEncoder() {
		return rightEncoder;
	}

	public SensorReading setRightEncoder(Integer rightEncoder) {
		this.rightEncoder = rightEncoder;
		return this;
	}
}
