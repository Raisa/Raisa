package raisa.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.config.VisualizerConfig;

public class VisualizationOptionsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JCheckBox particles;
	private JCheckBox trail;
	private JCheckBox simulator;
	private JCheckBox map;
	private JCheckBox robot;
	private JCheckBox irScan;
	private JCheckBox sonarScan;
	
	public VisualizationOptionsPanel() {
		setBorder(new TitledBorder("Visualization options"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		
		particles = new JCheckBox("Particles");
		particles.setSelected(true);
		particles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayParticles(particles.isSelected());
			}
		});

		add(particles);
		
		trail = new JCheckBox("Trail");
		trail.setSelected(true);
		trail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayTrail(trail.isSelected());
			}
		});
		add(trail);

		robot = new JCheckBox("Robot");
		robot.setSelected(true);
		robot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayRobot(robot.isSelected());
			}
		});
		add(robot);

		simulator = new JCheckBox("Simulator");
		simulator.setSelected(true);
		simulator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplaySimulator(simulator.isSelected());
			}
		});
		add(simulator);

		irScan = new JCheckBox("Infra red scanner");
		irScan.setSelected(true);
		irScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayIrScan(irScan.isSelected());
			}
		});
		add(irScan);
		
		sonarScan = new JCheckBox("Ultra sound scanner");
		sonarScan.setSelected(true);
		sonarScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplaySonarScan(sonarScan.isSelected());
			}
		});
		add(sonarScan);

		map = new JCheckBox("Map");
		map.setSelected(true);
		map.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualizerConfig.getInstance().setDisplayMap(map.isSelected());
			}
		});
		add(map);
	}
	
}
