package raisa.ui.tool;

import java.awt.event.MouseEvent;

import raisa.ui.VisualizerFrame;
import raisa.util.Vector2D;

public class MeasureTool extends BasicTool {
	public MeasureTool(VisualizerFrame frame) {
		super(frame);
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Vector2D mouseFrom, Vector2D mouseTo) {
		super.mouseDragged(mouseEvent, mouseFrom, mouseTo);
	}
}
