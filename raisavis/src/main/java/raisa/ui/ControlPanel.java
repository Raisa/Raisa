package raisa.ui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import raisa.comms.BasicController;
import raisa.comms.Communicator;
import raisa.session.SessionWriter;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public ControlPanel(VisualizerFrame frame, VisualizerPanel visualizer, BasicController controller, Communicator communicator, SessionWriter sessionWriter) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		ToolPanel toolPanel = new ToolPanel(frame);
		toolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(toolPanel);
		add(new CommunicatorPanel(communicator));
		add(new MovementPanel(controller));
		add(new OtherControlsPanel(controller, sessionWriter));
	}

}
