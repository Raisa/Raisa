package raisa.ui.tool;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import raisa.ui.VisualizerFrame;

public class DrawTool extends BasicTool  {
	public DrawTool(VisualizerFrame frame) {
		super(frame);
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent, Float mouse) {
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent, Float mouseFrom, Float mouseTo) {
		super.mouseDragged(mouseEvent, mouseFrom, mouseTo);
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent, Float mouse) {
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent, Float mouseDragStart) {
	}


}
