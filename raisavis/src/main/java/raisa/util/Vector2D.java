package raisa.util;

import java.awt.geom.Point2D;

public class Vector2D extends Point2D.Float {
	private static final long serialVersionUID = 1L;

	public Vector2D(float x, float y) {
		super(x, y);
	}

	public Vector2D(Float p) {
		this(p.x, p.y);
	}

	public Vector2D() {
	}
}
