package raisa.ui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import raisa.ui.VisualizerFrame;

public class MeasureTool extends BasicTool {
	public MeasureTool(VisualizerFrame frame) {
		super(frame);
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Float mouseFrom, Float mouseTo) {
		super.mouseDragged(mouseEvent, mouseFrom, mouseTo);
	}
}
