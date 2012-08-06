package raisa.simulator;

import raisa.util.Vector3D;

public class SensorReading {
	private Long timestamp;
	private Integer messageNumber;
	private Vector3D gyro;
	private Vector3D acceleration;
	private Integer irDistance1;
	private Integer sonarDistance1;
	private Integer irDirection1;
	private Integer sonarDirection1;
	private Integer irDistance2;
	private Integer sonarDistance2;
	private Integer irDirection2;
	private Integer sonarDirection2;
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
		addField(builder, "Sd", sonarDistance1);
		addField(builder, "Id", irDistance1);
		addField(builder, "SR", sonarDirection1);
		addField(builder, "IR", irDirection1);
		addField(builder, "Cd", compassHeading);
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

	public Integer getIrDistance1() {
		return irDistance1;
	}

	public SensorReading setIrDistance1(Integer irDistance1) {
		this.irDistance1 = irDistance1;
		return this;
	}

	public Integer getSonarDistance1() {
		return sonarDistance1;
	}

	public SensorReading setSonarDistance1(Integer sonarDistance1) {
		this.sonarDistance1 = sonarDistance1;
		return this;
	}

	public Integer getIrDirection1() {
		return irDirection1;
	}

	public SensorReading setIrDirection1(Integer irDirection1) {
		this.irDirection1 = irDirection1;
		return this;
	}

	public Integer getSonarDirection1() {
		return sonarDirection1;
	}

	public SensorReading setSonarDirection1(Integer sonarDirection1) {
		this.sonarDirection1 = sonarDirection1;
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

	public Integer getIrDistance2() {
		return irDistance2;
	}

	public void setIrDistance2(Integer irDistance2) {
		this.irDistance2 = irDistance2;
	}

	public Integer getSonarDistance2() {
		return sonarDistance2;
	}

	public void setSonarDistance2(Integer sonarDistance2) {
		this.sonarDistance2 = sonarDistance2;
	}

	public Integer getIrDirection2() {
		return irDirection2;
	}

	public void setIrDirection2(Integer irDirection2) {
		this.irDirection2 = irDirection2;
	}

	public Integer getSonarDirection2() {
		return sonarDirection2;
	}

	public void setSonarDirection2(Integer sonarDirection2) {
		this.sonarDirection2 = sonarDirection2;
	}
}
