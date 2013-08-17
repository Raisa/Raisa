package raisa.ui.options;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import raisa.config.VisualizerConfig;
import raisa.ui.MapAreaElementEnum;

class MapAreaOptions extends JPanel {		
	private static final long serialVersionUID = 1L;		

	private JSlider particleMinAge;

	public MapAreaOptions() {
		setBorder(new TitledBorder("Map area"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setAlignmentY(Component.TOP_ALIGNMENT);

		this.add(new MapAreaElementCheckBox("Particles", MapAreaElementEnum.PARTICLES));
		this.add(new MapAreaElementCheckBox("Trail", MapAreaElementEnum.ROBOT_TRAIL));
		this.add(new MapAreaElementCheckBox("Robot", MapAreaElementEnum.ROBOT));
		this.add(new MapAreaElementCheckBox("Infrared scanner", MapAreaElementEnum.INFRARED_SCANNER));
		this.add(new MapAreaElementCheckBox("Ultrasonic scanner", MapAreaElementEnum.ULTRASONIC_SCANNER));
		this.add(new MapAreaElementCheckBox("Map", MapAreaElementEnum.MAP));
		this.add(new MapAreaElementCheckBox("Landmarks", MapAreaElementEnum.LANDMARKS));		
		
		JLabel particleAgeLabel = new JLabel("Min particle age"); 
		particleAgeLabel.setToolTipText("Minimum age (in generations) for particles to display");
		add(particleAgeLabel);
		particleMinAge = new JSlider(0, 20, 0);
		particleMinAge.setToolTipText("Minimum age (in generations) for particles to display");
		particleMinAge.setPreferredSize(new Dimension(50, 20));
		particleMinAge.setSnapToTicks(true);
		particleMinAge.setMajorTickSpacing(5);
		particleMinAge.setMinorTickSpacing(1);
		particleMinAge.setPaintTicks(true);
		particleMinAge.setPaintLabels(true);
		particleMinAge.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				VisualizerConfig.getInstance().setDisplayMinAgeForParticles(source.getValue());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(particleMinAge);
	}
	
	private class MapAreaElementCheckBox extends JCheckBox implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private MapAreaElementEnum mapAreaElement;
		
		public MapAreaElementCheckBox(String text, MapAreaElementEnum mapAreaElement) {
			super(text);
			this.mapAreaElement = mapAreaElement;
			VisualizerConfig config = VisualizerConfig.getInstance();
			this.setSelected(config.getDisplayedMapAreaElements().contains(mapAreaElement));
			this.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			VisualizerConfig config = VisualizerConfig.getInstance();
			if (this.isSelected()) {
				config.addDisplayedMapAreaElement(mapAreaElement);
			} else {
				config.removeDisplayedMapAreaElement(mapAreaElement);
			}
			config.notifyVisualizerConfigListeners();
		}
		
	}

}
