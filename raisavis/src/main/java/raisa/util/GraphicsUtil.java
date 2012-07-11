package raisa.util;

import java.awt.Color;

public class GraphicsUtil {
	public static final Color makeTransparentColor(Color color, float transparency) {
		return new Color(color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, transparency);
	}
}
