package raisa.ui.tool;

import java.awt.event.MouseEvent;

import raisa.ui.VisualizerFrame;
import raisa.util.Vector2D;

public class DrawTool extends BasicTool {
	public DrawTool(VisualizerFrame frame) {
		super(frame);
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo) {
		if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) > 0) {
			if (mouseEvent.isControlDown()) {
			} else {
				drawLine(mouseEvent, mouseFrom, mouseTo);
				getVisualizerFrame().repaint();
			}
		} else {
			super.mouseDragged(mouseEvent, mouseFrom, mouseTo);
		}
	}

	private void drawLine(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo) {
		int length = (int) mouseTo.distance(mouseFrom);
		float dx = (mouseTo.x - mouseFrom.x) / length;
		float dy = (mouseTo.y - mouseFrom.y) / length;
		Vector2D position = new Vector2D();
		for (int i = 0; i < length; ++i) {
			position.x = mouseFrom.x + i * dx;
			position.y = mouseFrom.y + i * dy;
			drawPoint(position, !mouseEvent.isShiftDown());
		}		
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent, Vector2D mouse) {
		if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) > 0) {
			if (mouseEvent.isControlDown()) {
			} else {
				getVisualizerFrame().pushUserEditUndoLevel();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo) {
		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			if (mouseEvent.isControlDown()) {
				getVisualizerFrame().pushUserEditUndoLevel();
				drawLine(mouseEvent, mouseFrom, mouseTo);
				getVisualizerFrame().repaint();
			} else {
				drawPoint(mouseTo, !mouseEvent.isShiftDown());
				getVisualizerFrame().repaint();
			}
		}
	}

	private void drawPoint(Vector2D mouse, boolean isBlocked) {
		Vector2D worldPosition = getVisualizerFrame().toWorld(mouse);
		getVisualizerFrame().setUserPosition(worldPosition, isBlocked);
	}
}
