package raisa.ui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import raisa.ui.VisualizerFrame;

public abstract class BasicTool implements Tool {
	private VisualizerFrame visualizerFrame;

	public BasicTool(VisualizerFrame frame) {
		this.visualizerFrame = frame;
	}

	public VisualizerFrame getVisualizerFrame() {
		return visualizerFrame;
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent, Float mouse) {
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Float mouseFrom, Float mouseTo) {
		if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) > 0) {
			float dx = (mouseFrom.x - mouseTo.x) / visualizerFrame.getScale();
			float dy = (mouseFrom.y - mouseTo.y) / visualizerFrame.getScale();
			visualizerFrame.panCameraBy(dx, dy);
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent, Float mouse) {
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent, Float mouseDragStart) {
	}

}
