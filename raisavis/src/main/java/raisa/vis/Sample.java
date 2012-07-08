package raisa.vis;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Sample {
	public String sampleString;
	public Map<String, Object> data = new HashMap<String, Object>();
	private Robot robot;
	private final static float G = 9.80665f;

	public Sample(String sample) {
		sampleString = sample;
		if (!isValid(sample)) {
			System.out.println("INVALID SAMPLE! \"" + sample + "\"");
		} else {
			String[] sampleParts = sample.split("[;]");
			for (String part : sampleParts) {
				String value = StringUtils.substring(part, 2);
				if ("STA".equals(part)) {
				} else if ("END".equals(part)) {
				} else if (part.startsWith("IR")) {
					float angle = (float) Math.toRadians(Integer.parseInt(value));
					angle = angle - (float)Math.PI / 2.0f;
					data.put("ir", angle);
				} else if (part.startsWith("ID")) {
					float irSensorValue = Integer.parseInt(value);
					float distance = 10650.08f * (float)Math.pow(irSensorValue, -0.935f) - 10.0f; // cm
					if (distance > 20.0f && distance < 150.0f) {
						data.put("id", distance);
					}
				} else if (part.startsWith("SR")) {
					float angle = (float) Math.toRadians(Integer.parseInt(value));
					angle = angle - (float)Math.PI / 2.0f;
					data.put("sr", angle);
				} else if (part.startsWith("SD")) {
					float srSensorValue = Integer.parseInt(value);
					float distance = (srSensorValue / 2.0f) * 2.54f; // cm
					if (distance > 15.0f && distance < 645.0f) {
						data.put("sd", distance);
					}
				} else if (part.startsWith("CD")) {
					float compass = (float) (Math.toRadians(Integer.parseInt(value)) - Math.PI * 0.5f);
					data.put("cd", compass);
					data.put("heading", compass);
				} else if (part.startsWith("AX")) {
					float accelerationX = (G * ((-Float.parseFloat(value)) - 24)) / 1000 ;
					data.put("ax", accelerationX);
				} else if (part.startsWith("AY")) {
					float accelerationY = (G * ((Float.parseFloat(value) - 59))) / 1000;
					data.put("az", accelerationY);
				} else if (part.startsWith("AZ")) {
					float accelerationZ = (G * ((-Float.parseFloat(value)) - 8)) / 1000;
					data.put("ay", accelerationZ);
				} else if (part.startsWith("GX")) {
					float gyroX = Float.parseFloat(value);
					data.put("gx", -gyroX / 1000);
				} else if (part.startsWith("GY")) {
					float gyroY = Float.parseFloat(value);
					data.put("gz", gyroY / 1000);
				} else if (part.startsWith("GZ")) {
					float gyroZ = Float.parseFloat(value);
					data.put("gy", -gyroZ / 1000);					
				} else if (part.startsWith("RL")) {
					float ticks = Float.parseFloat(value);
					data.put("rl", ticks);
				} else if (part.startsWith("RR")) {
					float ticks = Float.parseFloat(value);
					data.put("rr", ticks);
				} else {
				}
			}
		}
	}

	public boolean isIrSpot() {
		return (data.containsKey("ir") && data.containsKey("id"));
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}
	
	public Robot getRobot() {
		return robot;
	}
	
	public float getHeading() {
		return (Float) data.get("heading");
	}

	public Spot getIrSpot() {
		float x = robot.getPosition().x;
		float y = robot.getPosition().y;
		float heading = (Float) data.get("heading");
		float angle = 0.0f;
		float distance = 0.0f;
		if (data.containsKey("ir") && data.containsKey("id")) {
			angle = (Float) data.get("ir");
			distance = (Float) data.get("id");
		}
		angle += heading - (float)Math.PI * 0.5f;
		Spot spot = new Spot(x + (float) Math.cos(angle) * distance, y + (float) Math.sin(angle) * distance);
		return spot;
	}

	public Spot getSrSpot() {
		float x = robot.getPosition().x;
		float y = robot.getPosition().y;
		float heading = robot.heading;
		float angle = 0.0f;
		float distance = 0.0f;
		if (data.containsKey("sr")) {
			angle = (Float) data.get("sr");
			distance = (Float) data.get("sd");
		}
		angle += heading - (float)Math.PI * 0.5f;
		Spot spot = new Spot(x + (float) Math.cos(angle) * distance, y + (float) Math.sin(angle) * distance);
		return spot;
	}

	public static boolean isValid(String sample) {
		return sample.matches("STA;([A-Z]+[-]?[0-9]+;)*END;[\n\r]*");
	}

	public float getIrDirection() {
		return (Float) data.get("ir");
	}
	
	public Vector3D getAcceleration() {
		float x = (Float) (data.get("ax")==null?0.0f:data.get("ax"));
		float y = (Float) (data.get("ay")==null?0.0f:data.get("ay"));
		float z = (Float) (data.get("az")==null?0.0f:data.get("az"));
		return new Vector3D(x, y, z);
	}
	
	public Vector3D getAngularAcceleration() {
		float x = (Float) (data.get("gx")==null?0.0f:data.get("gx"));
		float y = (Float) (data.get("gy")==null?0.0f:data.get("gy"));
		float z = (Float) (data.get("gz")==null?0.0f:data.get("gz"));
		return new Vector3D(x, y, z);
	}	
	
	public float getLeftWheelTicks() {
		return (Float) (data.get("rl")==null?0.0f:data.get("rl"));
	}
	
	public float getRightWheelTicks() {
		return (Float) (data.get("rl")==null?0.0f:data.get("rr"));
	}	
	
}
