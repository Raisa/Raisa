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

class MapAreaOptions extends JPanel {		
	private static final long serialVersionUID = 1L;		
	
	private JCheckBox particles;
	private JCheckBox trail;
	private JCheckBox simulator;
	private JCheckBox map;
	private JCheckBox robot;
	private JCheckBox irScan;
	private JCheckBox sonarScan;
	private JSlider particleMinAge;

	public MapAreaOptions() {
		setBorder(new TitledBorder("Map area"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);

		particles = new JCheckBox("Particles");
		particles.setSelected(true);
		particles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayParticles(particles.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});

		add(particles);
		
		trail = new JCheckBox("Trail");
		trail.setSelected(true);
		trail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayTrail(trail.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(trail);

		robot = new JCheckBox("Robot");
		robot.setSelected(true);
		robot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayRobot(robot.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(robot);

		simulator = new JCheckBox("Simulator");
		simulator.setSelected(true);
		simulator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplaySimulator(simulator.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(simulator);

		irScan = new JCheckBox("Infra red scanner");
		irScan.setSelected(true);
		irScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayIrScan(irScan.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(irScan);
		
		sonarScan = new JCheckBox("Ultra sound scanner");
		sonarScan.setSelected(true);
		sonarScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplaySonarScan(sonarScan.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(sonarScan);

		map = new JCheckBox("Map");
		map.setSelected(true);
		map.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayMap(map.isSelected());
				VisualizerConfig.getInstance().notifyVisualizerConfigListeners();
			}
		});
		add(map);
		
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
	
}
