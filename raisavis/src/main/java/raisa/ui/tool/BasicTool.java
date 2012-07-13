package raisa.ui.tool;

import java.awt.event.MouseEvent;

import raisa.ui.VisualizerFrame;
import raisa.util.Vector2D;

public abstract class BasicTool implements Tool {
	private VisualizerFrame visualizerFrame;

	public BasicTool(VisualizerFrame frame) {
		this.visualizerFrame = frame;
	}

	public VisualizerFrame getVisualizerFrame() {
		return visualizerFrame;
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent, Vector2D mouse) {
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo) {
		if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) > 0) {
			float dx = (mouseFrom.x - mouseTo.x) / visualizerFrame.getScale();
			float dy = (mouseFrom.y - mouseTo.y) / visualizerFrame.getScale();
			visualizerFrame.panCameraBy(dx, dy);
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent, Vector2D mouse) {
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo) {
	}

}
