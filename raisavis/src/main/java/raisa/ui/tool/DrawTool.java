package raisa.ui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import raisa.ui.VisualizerFrame;
import raisa.util.Vector2D;

public class DrawTool extends BasicTool {
	public DrawTool(VisualizerFrame frame) {
		super(frame);
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Float mouseFrom, Float mouseTo) {
		if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) > 0) {
			int length = (int)mouseTo.distance(mouseFrom);
			float dx = (mouseTo.x - mouseFrom.x) / length;
			float dy = (mouseTo.y - mouseFrom.y) / length;
			Float position = new Float();
			for (int i = 0; i < length; ++i) {
				position.x = mouseFrom.x + i * dx;
				position.y = mouseFrom.y + i * dy;
				drawPoint(position, !mouseEvent.isShiftDown());
			}
		} else {
			super.mouseDragged(mouseEvent, mouseFrom, mouseTo);
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent, Float mouse) {
		if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) > 0) {
			drawPoint(mouse, !mouseEvent.isShiftDown());
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent, Float mouse) {
		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			drawPoint(mouse, !mouseEvent.isShiftDown());
		}
	}

	private void drawPoint(Float mouse, boolean isBlocked) {
		Vector2D worldPosition = getVisualizerFrame().toWorld(new Vector2D(mouse));
		getVisualizerFrame().setPosition(worldPosition, isBlocked);
	}
}
