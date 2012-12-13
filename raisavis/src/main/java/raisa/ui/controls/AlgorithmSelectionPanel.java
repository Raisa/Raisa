package raisa.ui.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import raisa.config.LocalizationModeEnum;
import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.domain.AlgorithmTypeEnum;

public class AlgorithmSelectionPanel extends ControlSubPanel implements VisualizerConfigListener {
	private static final long serialVersionUID = 1L;
	
	private JComboBox localizationModeBox;
	
	public AlgorithmSelectionPanel() {
		setBorder(new TitledBorder("Algorithms"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		
		createLocalizationModeControl();
		this.add(new AlgorithmSelectionCheckBox("RANSAC", AlgorithmTypeEnum.RANSAC_LANDMARK_EXTRACTION));
		this.add(new AlgorithmSelectionCheckBox("Spikes", AlgorithmTypeEnum.SPIKES_LANDMARK_EXTRACTION));
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}
	
	private void createLocalizationModeControl() {
		final JLabel label = new JLabel("Localization:");
		final String[] targets = { "None", "Particle filter" };
		localizationModeBox = new JComboBox(targets);
		final VisualizerConfig config = VisualizerConfig.getInstance();
		localizationModeBox.setMaximumSize(new Dimension(150,50));
		localizationModeBox.setSelectedIndex(config.getLocalizationMode().getIndex());
		localizationModeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch (localizationModeBox.getSelectedIndex()) {
				case 0:
					config.setLocalizationMode(LocalizationModeEnum.NONE);
					break;
				default:
					config.setLocalizationMode(LocalizationModeEnum.PARTICLE_FILTER);
					break;	
				}
				config.notifyVisualizerConfigListeners();
			}			
		});
		add(label);
		add(localizationModeBox);
	}	
	
	private class AlgorithmSelectionCheckBox extends JCheckBox implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private AlgorithmTypeEnum algorithmType;
		
		public AlgorithmSelectionCheckBox(String text, AlgorithmTypeEnum algorithmType) {
			super(text);
			this.algorithmType = algorithmType;
			VisualizerConfig config = VisualizerConfig.getInstance();
			this.setSelected(config.getActivatedAlgorithms().contains(algorithmType));
			this.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			VisualizerConfig config = VisualizerConfig.getInstance();
			if (this.isSelected()) {
				config.addActivatedAlgorithm(algorithmType);
			} else {
				config.removeActivatedAlgorithm(algorithmType);
			}
			config.notifyVisualizerConfigListeners();
		}
		
	}

	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (config.isChanged(VisualizerConfigItemEnum.LOCALIZATION_MODE)) {
			localizationModeBox.setSelectedIndex(config.getLocalizationMode().getIndex());
		}
	}
	
	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.ALGORITHM_SELECTION;
	}

}
