package raisa.ui.tool;

import java.awt.event.MouseEvent;

import raisa.util.Vector2D;

public interface Tool {
	void mouseMoved(MouseEvent mouseEvent, Vector2D mouse);
	void mouseDragged(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo);
	void mousePressed(MouseEvent mouseEvent, Vector2D mouse);
	void mouseReleased(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo);
}
