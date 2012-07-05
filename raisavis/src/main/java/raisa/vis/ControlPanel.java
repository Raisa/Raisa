package raisa.vis;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public ControlPanel(VisualizerPanel visualizer, BasicController controller, Communicator communicator) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new CommunicatorPanel(communicator));
		add(new MovementPanel(controller));
		add(new OtherControlsPanel(controller));
	}

}
