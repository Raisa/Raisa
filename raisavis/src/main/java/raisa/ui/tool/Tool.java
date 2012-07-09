package raisa.ui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

public interface Tool {
	void mouseMoved(MouseEvent mouseEvent, Float mouse);
	void mouseDragged(MouseEvent mouseEvent, Float mouseFrom, Float mouseTo);
	void mousePressed(MouseEvent mouseEvent, Float mouse);
	void mouseReleased(MouseEvent mouseEvent, Float mouseDragStart);
}
