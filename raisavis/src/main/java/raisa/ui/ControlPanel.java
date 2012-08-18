package raisa.ui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import raisa.comms.BasicController;
import raisa.comms.Communicator;
import raisa.session.SessionWriter;
import raisa.simulator.RobotSimulator;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public ControlPanel(VisualizerFrame frame, VisualizerPanel visualizer, BasicController controller, Communicator communicator, SessionWriter sessionWriter, RobotSimulator robotSimulator) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		ToolPanel toolPanel = new ToolPanel(frame);
		toolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(toolPanel);
		add(new CommunicatorPanel(communicator));
		add(new MovementPanel(controller));
		add(new PanAndTiltSystemPanel(controller));
		add(new OtherControlsPanel(controller, sessionWriter, robotSimulator));		
	}

}
