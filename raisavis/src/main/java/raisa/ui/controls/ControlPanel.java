package raisa.ui.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.comms.Communicator;
import raisa.comms.controller.BasicController;
import raisa.comms.controller.PidController;
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
			BasicController basicController, 
			PidController pidController,
			Communicator communicator, 
			SessionWriter sessionWriter, 
			RobotSimulator robotSimulator) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		TitledBorder border = new TitledBorder("Controls");
		this.setPreferredSize(new Dimension(200, 300));
		setBorder(border);
		ToolPanel toolPanel = new ToolPanel(frame);
		toolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		subpanels.add(toolPanel);
		subpanels.add(new CommunicatorPanel(communicator));
		subpanels.add(new MovementPanel(frame, basicController, pidController));
		subpanels.add(new PanAndTiltSystemPanel(basicController));
		subpanels.add(new OtherControlsPanel(basicController, sessionWriter, robotSimulator));	
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
