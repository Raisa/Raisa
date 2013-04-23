package raisa.ui.controls;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import raisa.comms.Communicator;
import raisa.comms.controller.BasicController;
import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.session.SessionWriter;
import raisa.simulator.RobotSimulator;
import raisa.ui.VisualizerFrame;
import raisa.ui.VisualizerPanel;

public class ControlPanel extends JPanel implements VisualizerConfigListener {
	private static final long serialVersionUID = 1L;

	private List<ControlSubPanel> subpanels = new ArrayList<ControlSubPanel>();	
	
	public ControlPanel(
			VisualizerFrame frame, 
			VisualizerPanel visualizer, 
			BasicController controller, 
			Communicator communicator, 
			SessionWriter sessionWriter, 
			RobotSimulator robotSimulator) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		ToolPanel toolPanel = new ToolPanel(frame);
		toolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		subpanels.add(toolPanel);
		subpanels.add(new CommunicatorPanel(communicator));
		subpanels.add(new MovementPanel(controller));
		subpanels.add(new PanAndTiltSystemPanel(controller));
		subpanels.add(new OtherControlsPanel(controller, sessionWriter, robotSimulator));	
		subpanels.add(new AlgorithmSelectionPanel());
		this.setDisplayedPanels(VisualizerConfig.getInstance());
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}

	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (!config.isChanged(VisualizerConfigItemEnum.DISPLAYED_CONTROLS)) {
			return;
		}
		setDisplayedPanels(config);
		this.validate();
		this.repaint();
	}		
	
	private void setDisplayedPanels(VisualizerConfig config) {
		Set<ControlTypeEnum> displayedControls = config.getDisplayedControls();
		for (ControlSubPanel subpanel : subpanels) {
			subpanel.setDisplayed(displayedControls.contains(subpanel.getControlSubPanelType()));
		}
		this.removeAll();
		for (ControlSubPanel subpanel : subpanels) {
			if (subpanel.isDisplayed()) {
				this.add(subpanel);
			}
		}
	}
	
}
