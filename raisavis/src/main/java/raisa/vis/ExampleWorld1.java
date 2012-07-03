package raisa.vis;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ExampleWorld1 {
	public String sample(float x, float y, float angle) {
		List<Shape> scene = new ArrayList<Shape>();
		scene.add(new Rectangle2D.Float(-150, -150, 100, 100));
		scene.add(new Rectangle2D.Float(-50, 100, 300, 100));
		scene.add(new Rectangle2D.Float(150, -150, 100, 100));
		scene.add(new Rectangle2D.Float(0, 50, 25, 100));
		scene.add(new Ellipse2D.Float(0, -120, 50, 50));
		scene.add(new Ellipse2D.Float(200, -20, 80, 80));

		float shortestDistance = Float.MAX_VALUE;
		for (Shape shape : scene) {
			float distance = trace(x, y, angle, shape, 250.0f);
			if (distance < 250.0f) {
				shortestDistance = Math.min(shortestDistance, distance);
			}
		}

		String ir = "IR%d;";
		if (shortestDistance < 250.0f) {
			ir += "ID%d;";			
		}
		
		float a = (float) Math.toDegrees(angle);
		// String sampleString = String.format("J%1$3d,%2$3d\n", (int)a, (int)
		// distance);
		String sampleString = String.format("STA;" + ir + "END;\n", (int) a,
				(int) shortestDistance);
		// System.out.print(sampleString);
		return sampleString;
	}

	private float trace(float x, float y, float angle, Shape shape, float maxDistance) {
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
