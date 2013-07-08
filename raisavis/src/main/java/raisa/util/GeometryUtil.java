package raisa.util;



public class GeometryUtil {

	public static Vector2D calculatePosition(Vector2D position, float angle, float distance) {
		Vector2D newPosition = new Vector2D(position.x, position.y);
		double a = angle - Math.PI * 0.5f;
		newPosition.x += Math.cos(a) * distance;
		newPosition.y += Math.sin(a) * distance;
		return newPosition;
	}
	
	public static float headingToAtan2Angle(float heading) {
		if (heading < 3 * Math.PI / 2.0d) {
			return (float)(heading - Math.PI / 2.0d);
		} else {
			return (float)(heading - 2.5d * Math.PI);
		}
	}
	
	public static float differenceBetweenAngles(float firstAngle, float secondAngle) {
		float difference = secondAngle - firstAngle;
		if (difference < -Math.PI) 
			return (float)(2 * Math.PI + difference);
		if (difference > Math.PI) 
	        return (float)(- 2 * Math.PI + difference);
		return difference;
	 }

}
