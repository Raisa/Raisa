package raisa.util;

import java.awt.geom.Line2D;

public class Segment2D extends Line2D.Float {
	private static final long serialVersionUID = 1L;
	
	private float slope;
	private float intersect;
	
	public Segment2D(
			float slope, 
			float intersect,
			float minX,
			float minY,
			float maxX,
			float maxY) {
		//super(minX, minX * slope + intersect, maxX, maxX * slope + intersect);
		
		// draw the line in a bounding box
		if ((minX * slope + intersect) > minY) {
			this.x1 = minX;
			this.y1 = minX * slope + intersect;
		} else {
			this.x1 = (minY - intersect) / slope;
			this.y1 = minY;
		}
		if ((maxX * slope + intersect) < maxY) {
			this.x2 = maxX;
			this.y2 = maxX * slope + intersect;
		} else {
			this.x2 = (maxY - intersect) / slope;
			this.y2 = maxY;
		}
		this.slope = slope;
		this.intersect = intersect;
	}
	
	public float getSlope() {
		return this.slope;
	}
	
	public float getIntersect() {
		return this.intersect;
	}
	
	public void setSlope(float slope) {
		this.slope = slope;
	}
	
	public void setIntersect(float intersect) {
		this.intersect = intersect;
	}
	
}
