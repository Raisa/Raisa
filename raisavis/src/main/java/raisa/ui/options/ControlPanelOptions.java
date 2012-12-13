package raisa.ui.options;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.config.VisualizerConfig;
import raisa.ui.controls.ControlTypeEnum;

public class ControlPanelOptions extends JPanel {

	private static final long serialVersionUID = 1L;	
	
	public ControlPanelOptions() {
		setBorder(new TitledBorder("Controls"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setAlignmentY(Component.TOP_ALIGNMENT);
		
		this.add(new ControlTypeCheckBox("Communicator", ControlTypeEnum.COMMUNICATOR));
		this.add(new ControlTypeCheckBox("Drawing tool", ControlTypeEnum.DRAWING_TOOL));
		this.add(new ControlTypeCheckBox("Movement", ControlTypeEnum.MOVEMENT));
		this.add(new ControlTypeCheckBox("Pan & tilt", ControlTypeEnum.PAN_AND_TILT));
		this.add(new ControlTypeCheckBox("Other", ControlTypeEnum.OTHER));
		this.add(new ControlTypeCheckBox("Algorithms", ControlTypeEnum.ALGORITHM_SELECTION));
	}
	
	private class ControlTypeCheckBox extends JCheckBox implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private ControlTypeEnum controlType;
		
		public ControlTypeCheckBox(String text, ControlTypeEnum controlType) {
			super(text);
			this.controlType = controlType;
			VisualizerConfig config = VisualizerConfig.getInstance();
			this.setSelected(config.getDisplayedControls().contains(controlType));
			this.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			VisualizerConfig config = VisualizerConfig.getInstance();
			if (this.isSelected()) {
				config.addDisplayedControl(controlType);
			} else {
				config.removeDisplayedControl(controlType);
			}
			config.notifyVisualizerConfigListeners();
		}
		
	}

}
