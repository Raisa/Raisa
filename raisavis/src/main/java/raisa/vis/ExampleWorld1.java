package raisa.vis;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ExampleWorld1 {
	public String sample(float x, float y, float heading, float angle) {
		List<Shape> scene = new ArrayList<Shape>();
		scene.add(new Rectangle2D.Float(-150, -150, 100, 100));
		scene.add(new Rectangle2D.Float(-50, 100, 300, 100));
		scene.add(new Rectangle2D.Float(150, -150, 100, 100));
		scene.add(new Rectangle2D.Float(0, 50, 25, 100));
		scene.add(new Ellipse2D.Float(0, -120, 50, 50));
		scene.add(new Ellipse2D.Float(200, -20, 80, 80));

		float shortestDistance = Float.MAX_VALUE;
		for (Shape shape : scene) {
			float distance = trace(x, y, heading + angle, shape, 250.0f);
			if (distance < 250.0f) {
				shortestDistance = Math.min(shortestDistance, distance);
			}
		}
		shortestDistance = Math.min(250.0f, shortestDistance);

		float encodedDistance = 1.0f / (float)Math.pow((shortestDistance + 10.0f) / 10650.08f, 1.0f / 0.935f);
		
		String ir = "IR%d;";
		if (shortestDistance < 250.0f) {
			ir += "ID%d;";			
		}
				
		float a = (float) Math.toDegrees(angle);
		a += 90;
		while (a < 0) {
			a += 360.0f;			
		}
		while (a > 360.0f) {
			a -= 360.0f;
		}
		// String sampleString = String.format("J%1$3d,%2$3d\n", (int)a, (int)
		// distance);
		String cd = "CD%d;";
		String sampleString = String.format("STA;" + ir + cd + "RL1;RR1;END;\n", (int) a, (int) encodedDistance, (int)Math.toDegrees(heading));
		//System.out.print(sampleString);
		return sampleString;
	}

	private float trace(float x, float y, float angle, Shape shape, float maxDistance) {
		angle = angle - (float)Math.PI * 0.5f;
		float dx = (float)Math.cos(angle);
		float dy = (float)Math.sin(angle);
		
		for (float currentDistance = 0.0f; currentDistance < maxDistance; currentDistance += 1.0f) {
			if (shape.contains(x, y)) {
				return currentDistance;
			}
			x += dx;
			y += dy;
		}
		
		return maxDistance;
	}
}
