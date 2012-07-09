package raisa.ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import raisa.comms.BasicController;
import raisa.comms.Communicator;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public ControlPanel(VisualizerPanel visualizer, BasicController controller, Communicator communicator) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new CommunicatorPanel(communicator));
		add(new MovementPanel(controller));
		add(new OtherControlsPanel(controller));
	}

}
