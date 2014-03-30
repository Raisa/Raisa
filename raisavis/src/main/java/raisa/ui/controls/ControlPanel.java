package raisa.ui.controls;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.comms.Communicator;
import raisa.comms.controller.BasicController;
import raisa.comms.controller.PidController;
import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.domain.WorldModel;
import raisa.session.SessionWriter;
import raisa.simulator.RobotSimulator;
import raisa.ui.VisualizerFrame;
import raisa.ui.VisualizerPanel;

public class ControlPanel extends JPanel implements VisualizerConfigListener {
	private static final long serialVersionUID = 1L;

	private final List<ControlSubPanel> subpanels = new ArrayList<ControlSubPanel>();

	public ControlPanel(
			VisualizerFrame frame,
			VisualizerPanel visualizer,
			WorldModel world,
			BasicController basicController,
			PidController pidController,
			Communicator communicator,
			SessionWriter sessionWriter,
			RobotSimulator robotSimulator) {
		setLayout(new GridBagLayout());
		TitledBorder border = new TitledBorder("Controls");
		setBorder(border);
		subpanels.add(new ToolPanel(frame));
		subpanels.add(new CommunicatorPanel(communicator));
		subpanels.add(new MovementPanel(frame, world, basicController, pidController));
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
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 2, 2, 2);
		for (ControlSubPanel subpanel : subpanels) {
			if (subpanel.isDisplayed()) {
				this.add(subpanel, c);
			}
		}
        JLabel padding = new JLabel();
        c.weighty = 1.0;
        this.add(padding, c);
	}

}
