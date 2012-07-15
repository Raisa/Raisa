package raisa.domain;

import raisa.util.Vector3D;


public class Sample {
	private String sampleString;
	private float infrared1Angle;
	private float infrared1Distance;
	private boolean infrared1MeasurementValid;
	private float ultrasound1Angle;
	private float ultrasound1Distance;
	private boolean ultrasound1MeasurementValid;
	private float compassDirection;
	private Vector3D acceleration = new Vector3D();
	private Vector3D gyro = new Vector3D();
	private int leftTrackTicks;
	private int rightTrackTicks;
	private int soundIntensity;
	private long timestampMillis;
	
	public Sample() {		
	}
	
	public Sample(Sample copy) {
		this.sampleString = copy.sampleString;
		this.infrared1Angle = copy.infrared1Angle;
		this.infrared1Distance = copy.infrared1Distance;
		this.infrared1MeasurementValid = copy.infrared1MeasurementValid;
		this.ultrasound1Angle = copy.ultrasound1Angle;
		this.ultrasound1Distance = copy.ultrasound1Distance;
		this.ultrasound1MeasurementValid = copy.ultrasound1MeasurementValid;
		this.compassDirection = copy.compassDirection;
		this.acceleration = new Vector3D(copy.acceleration);
		this.gyro = new Vector3D(copy.gyro);
		this.leftTrackTicks = copy.leftTrackTicks;
		this.rightTrackTicks = copy.rightTrackTicks;
		this.soundIntensity = copy.soundIntensity;
		this.timestampMillis = copy.timestampMillis;
	}
	

	public String getSampleString() {
		return sampleString;
	}

	public void setSampleString(String sampleString) {
		this.sampleString = sampleString;
	}

	public boolean isUltrasound1MeasurementValid() {
		return ultrasound1MeasurementValid;
	}

	public void setUltrasound1MeasurementValid(boolean ultrasound1MeasurementValid) {
		this.ultrasound1MeasurementValid = ultrasound1MeasurementValid;
	}

	public void setInfrared1MeasurementValid(boolean infrared1MeasurementValid) {
		this.infrared1MeasurementValid = infrared1MeasurementValid;
	}

	public void setInfrared1Angle(float angle) {
		this.infrared1Angle = angle;
	}

	public void setInfrared1Distance(float distance) {
		this.infrared1Distance = distance;
	}

	public void setUltrasound1Angle(float angle) {
		this.ultrasound1Angle = angle;
	}

	public void setUltrasound1Distance(float distance) {
		this.ultrasound1Distance = distance;
	}

	public void setCompassDirection(float angle) {
		this.compassDirection = angle;
	}

	public void setAccelerationX(float accelerationX) {
		acceleration.setX(accelerationX);
	}

	public void setAccelerationY(float accelerationY) {
		acceleration.setY(accelerationY);
	}

	public void setAccelerationZ(float accelerationZ) {
		acceleration.setZ(accelerationZ);
	}

	public void setGyroX(float f) {
		gyro.setX(f);
	}

	public void setGyroY(float f) {
		gyro.setY(f);
	}

	public void setGyroZ(float f) {
		gyro.setZ(f);
	}

	public void setLeftTrackTicks(int ticks) {
		leftTrackTicks = ticks;
	}

	public void setRightTrackTicks(int ticks) {
		rightTrackTicks = ticks;
	}

	public void setSoundIntensity(int intensity) {
		soundIntensity = intensity;
	}	

	public void setTimestampMillis(long timestamp) {
		timestampMillis = timestamp;
	}		
	
	public float getInfrared1Angle() {
		return infrared1Angle;
	}

	public float getInfrared1Distance() {
		return infrared1Distance;
	}

	public float getUltrasound1Angle() {
		return ultrasound1Angle;
	}

	public float getUltrasound1Distance() {
		return ultrasound1Distance;
	}

	public float getCompassDirection() {
		return compassDirection;
	}

	public Vector3D getAcceleration() {
		return acceleration;
	}

	public Vector3D getGyro() {
		return gyro;
	}

	public int getLeftTrackTicks() {
		return leftTrackTicks;
	}

	public int getRightTrackTicks() {
		return rightTrackTicks;
	}

	public boolean isInfrared1MeasurementValid() {
		return infrared1MeasurementValid;
	}

	public int getSoundIntensity() {
		return soundIntensity;
	}		

	public long getTimestampMillis() {
		return timestampMillis;
	}
	
}
