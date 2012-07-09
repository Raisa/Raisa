package raisa.util;

import java.awt.geom.Point2D;


public class GeometryUtil {

	public static Vector2D calculatePosition(Point2D.Float position, float angle, float distance) {
		Vector2D newPosition = new Vector2D(position.x, position.y);
		double a = angle - Math.PI * 0.5f;
		newPosition.x += Math.cos(a) * distance;
		newPosition.y += Math.sin(a) * distance;
		return newPosition;
	}

}
