package raisa.comms;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.domain.Sample;

public class SampleParser {
	private static final Logger log = LoggerFactory.getLogger(SampleParser.class);
	public String sampleString;
	private final static float G = 9.80665f;

	public SampleParser() {
	}
	
	public Sample parse(String sampleString) {
		Sample sample = new Sample();
		sample.setSampleString(sampleString);
		boolean infrared1AngleIsValid = false;
		boolean infrared1DistanceIsValid = false;
		boolean ultrasound1AngleIsValid = false;
		boolean ultrasound1DistanceIsValid = false;
		if (!isValid(sampleString)) {
			log.warn("INVALID SAMPLE! \"{}\"", sampleString);
		} else {
			String[] sampleParts = sampleString.split("[;]");
			for (String part : sampleParts) {
				String value = StringUtils.substring(part, 2);
				if ("STA".equals(part)) {
				} else if ("END".equals(part)) {
				} else if (part.startsWith("IR")) {
					float angle = (float) Math.toRadians(Integer.parseInt(value));
					angle = angle - (float)Math.PI / 2.0f;
					sample.setInfrared1Angle(angle);
					infrared1AngleIsValid = true;
				} else if (part.startsWith("ID")) {
					float irSensorValue = Integer.parseInt(value);
					float distance = 10650.08f * (float)Math.pow(irSensorValue, -0.935f) - 10.0f; // cm
					if (distance > 20.0f && distance < 150.0f) {
						sample.setInfrared1Distance(distance);
						infrared1DistanceIsValid = true;
					}
				} else if(part.startsWith("Id")) {
					float distance = Integer.parseInt(value);
					if (distance > 20.0f && distance < 150.0f) {
						sample.setInfrared1Distance(distance);
						infrared1DistanceIsValid = true;
					}
				} else if (part.startsWith("SR")) {
					float angle = (float) Math.toRadians(Integer.parseInt(value));
					angle = angle - (float)Math.PI / 2.0f;
					sample.setUltrasound1Angle(angle);
					ultrasound1AngleIsValid = true;
				} else if (part.startsWith("SD")) {
					float srSensorValue = Integer.parseInt(value);
					float distance = (srSensorValue / 2.0f) * 2.54f; // cm
					if (distance > 15.0f && distance < 645.0f) {
						sample.setUltrasound1Distance(distance);
						ultrasound1DistanceIsValid = true;
					}
				} else if(part.startsWith("Sd")) {
					float distance = Integer.parseInt(value);
					if (distance > 15.0f && distance < 645.0f) {
						sample.setUltrasound1Distance(distance);
						ultrasound1DistanceIsValid = true;
					}
				} else if (part.startsWith("CD")) {
					float compass = (float) (Math.toRadians(Integer.parseInt(value)) - Math.PI * 0.5f);
					sample.setCompassDirection(compass);
				} else if (part.startsWith("AX")) {
					float accelerationX = (G * ((-Integer.parseInt(value)) - 24)) / 1000 ;
					sample.setAccelerationX(accelerationX);
				} else if (part.startsWith("Ax")) {
					sample.setAccelerationX(Float.parseFloat(value));
				} else if (part.startsWith("AY")) {
					float accelerationY = (G * ((Integer.parseInt(value) - 59))) / 1000;
					sample.setAccelerationY(accelerationY);
				} else if (part.startsWith("Ay")) {
					sample.setAccelerationY(Float.parseFloat(value));
				} else if (part.startsWith("AZ")) {
					float accelerationZ = (G * ((-Integer.parseInt(value)) - 8)) / 1000;
					sample.setAccelerationZ(accelerationZ);
				} else if (part.startsWith("Az")) {
					sample.setAccelerationZ(Float.parseFloat(value));
				} else if (part.startsWith("GX")) {
					float gyroX = Integer.parseInt(value);
					sample.setGyroX(-gyroX / 1000);
				} else if (part.startsWith("Gx")) {
					sample.setGyroX(Float.parseFloat(value));
				} else if (part.startsWith("GY")) {
					float gyroY = Integer.parseInt(value);
					sample.setGyroY(gyroY / 1000);
				} else if (part.startsWith("Gy")) {
					sample.setGyroY(Float.parseFloat(value));
				} else if (part.startsWith("GZ")) {
					float gyroZ = Integer.parseInt(value);
					sample.setGyroZ(-gyroZ / 1000);					
				} else if (part.startsWith("Gz")) {
					sample.setGyroZ(Float.parseFloat(value));					
				} else if (part.startsWith("RL")) {
					int ticks = Integer.parseInt(value);
					sample.setLeftTrackTicks(ticks);
				} else if (part.startsWith("RR")) {
					int ticks = Integer.parseInt(value);
					sample.setRightTrackTicks(ticks);
				} else if (part.startsWith("SB")) {
					int intensity = Integer.parseInt(value);
					sample.setSoundIntensity(intensity);	
				} else if (part.startsWith("TI")) {
					long timestampMillis = Long.parseLong(value);
					sample.setTimestampMillis(timestampMillis);										
				} else {
				}
			}
			sample.setInfrared1MeasurementValid(infrared1AngleIsValid && infrared1DistanceIsValid);
			sample.setUltrasound1MeasurementValid(ultrasound1AngleIsValid && ultrasound1DistanceIsValid);
		}		
		
		return sample;
	}

	public boolean isValid(String sample) {
		return sample.matches("STA;([A-Z][A-Za-z]+[-]?[0-9]*\\.?[0-9]+;)*END;[\n\r]*");
	}
}
