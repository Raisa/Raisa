package raisa.vis;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Sample {
	public Map<String, Object> data = new HashMap<String, Object>();

	public Sample(float x, float y, float heading, String sample) {
		data.put("x", x);
		data.put("y", y);
		data.put("heading", heading);
		if (!isValid(sample)) {
			System.out.println("INVALID SAMPLE! \"" + sample + "\"");
		} else {
			String[] sampleParts = sample.split("[;]");
			for (String part : sampleParts) {
				if ("STA".equals(part)) {
				} else if ("END".equals(part)) {
				} else if (part.startsWith("IR")) {
					float angle = (float) Math.toRadians(Integer.parseInt(part
							.substring(2)));
					data.put("ir", angle);
				} else if (part.startsWith("ID")) {
					float distance = Integer.parseInt(part.substring(2));
					data.put("id", distance);
				} else if (part.startsWith("SR")) {
					float angle = (float) Math.toRadians(Integer.parseInt(part
							.substring(2)));
					data.put("sr", angle);
				} else if (part.startsWith("SD")) {
					float distance = Integer.parseInt(part.substring(2));
					data.put("sd", distance);
				} else if (part.startsWith("CD")) {
					float compass = (float)Math.toRadians(Integer.parseInt(part.substring(2)));
					data.put("cd", compass);
					data.put("heading", compass);
				} else {
				}
			}
		}
	}

	public boolean isSpot() {
		return (data.containsKey("ir") && data.containsKey("id"));
	}

	public Spot getIrSpot() {
		float x = (Float) data.get("x");
		float y = (Float) data.get("y");
		float heading = (Float) data.get("heading");
		float angle = 0.0f;
		float distance = 0.0f;
		if (data.containsKey("ir")) {
			angle = (Float) data.get("ir");
			distance = (Float) data.get("id");
		}
		angle += heading;
		Spot spot = new Spot(x + (float) Math.cos(angle) * distance, y
				- (float) Math.sin(angle) * distance);
		return spot;
	}

	public Spot getSrSpot() {
		float x = (Float) data.get("x");
		float y = (Float) data.get("y");
		float heading = (Float) data.get("heading");
		float angle = 0.0f;
		float distance = 0.0f;
		if (data.containsKey("sr")) {
			angle = (Float) data.get("sr");
			distance = (Float) data.get("sd");
		}
		angle += heading;
		Spot spot = new Spot(x + (float) Math.cos(angle) * distance, y
				- (float) Math.sin(angle) * distance);
		return spot;
	}

	public Point2D.Float getRobot() {
		float x = (Float) data.get("x");
		float y = (Float) data.get("y");
		return new Point2D.Float(x, y);
	}

	public static boolean isValid(String sample) {
		return sample.matches("STA;([A-Z0-9]+;)*END;[\n\r]*");
	}
}
