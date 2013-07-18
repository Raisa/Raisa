package raisa.ui.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import raisa.comms.ControllerListener;
import raisa.comms.controller.BasicController;
import raisa.comms.controller.Controller;
import raisa.comms.controller.ControllerTypeEnum;
import raisa.comms.controller.PidController;
import raisa.config.VisualizerConfig;
import raisa.domain.WorldModel;
import raisa.ui.VisualizerFrame;
import raisa.ui.controls.sixaxis.SixaxisInput;

public class MovementPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;

	private final VisualizerFrame frame;
	private final WorldModel world;
	private JPanel controllerOptions = new JPanel();
	private JPanel manualControl = new JPanel();
	private JPanel pidControl = new JPanel();

	public MovementPanel(final VisualizerFrame frame, final WorldModel world, final BasicController basicController, final PidController pidController) {
		this.frame = frame;
		this.world = world;
		setBorder(new TitledBorder("Movement"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		createManualControlPanel(basicController);
		createPidControlPanel(pidController);

		final JComboBox controllerSelection = new JComboBox();
		controllerSelection.addItem("Manual control");
		controllerSelection.addItem("Pid control");
		controllerSelection.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				switch (controllerSelection.getSelectedIndex()) {
				case 0:
					controllerOptions.removeAll();
					controllerOptions.add(manualControl);
					VisualizerConfig.getInstance().setControllerType(ControllerTypeEnum.BASIC_CONTROLLER);
					break;
				default:
					controllerOptions.removeAll();
					controllerOptions.add(pidControl);
					VisualizerConfig.getInstance().setControllerType(ControllerTypeEnum.PID_CONTROLLER);
				}
				controllerOptions.validate();
				controllerOptions.repaint();
			}
		});
		add(controllerSelection);
		controllerOptions.add(manualControl);
		add(controllerOptions);
	}
	
	private void createPidControlPanel(final PidController controller) {
		pidControl.setLayout(new BoxLayout(pidControl, BoxLayout.Y_AXIS));
		JToggleButton addWaypoint = new JToggleButton("Add waypoint");
		addToolButton(addWaypoint);
		addWaypoint.setAlignmentX(Component.LEFT_ALIGNMENT);
		addWaypoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.selectedWaypointTool();
			}
		});
		JButton clearWaypoints = new JButton("Clear waypoints");
		clearWaypoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				world.getMotionPlan().clearRoute();
				frame.repaint();
			}
		});
		clearWaypoints.setAlignmentX(Component.LEFT_ALIGNMENT);
		pidControl.add(clearWaypoints);		
		pidControl.add(addWaypoint);
	}

	private void createManualControlPanel(final BasicController controller) {
		JButton forwardButton = new JButton("F");
		forwardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendForward();
			}			
		});
		JButton stopButton = new JButton("S");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendStop();
			}			
		});
		JButton backButton = new JButton("B");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendBack();
			}			
		});
		JButton leftButton = new JButton("L");
		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendLeft();
			}			
		});
		JButton rightButton = new JButton("R");
		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendRight();
			}			
		});
		
		manualControl.setLayout(new GridLayout(3, 3));
		final JLabel leftSpeedLabel = new JLabel("0", JLabel.CENTER);
		final JLabel rightSpeedLabel = new JLabel("0", JLabel.CENTER);
		manualControl.add(leftSpeedLabel);
		manualControl.add(forwardButton);
		manualControl.add(rightSpeedLabel);
		manualControl.add(leftButton);
		manualControl.add(stopButton);
		manualControl.add(rightButton);
		manualControl.add(new JSeparator());
		manualControl.add(backButton);
		manualControl.add(new JSeparator());
		
		manualControl.setPreferredSize(new Dimension(100, 90));
		manualControl.setMaximumSize(new Dimension(100, 90));
		setPreferredSize(new Dimension(190, 150));
		setMaximumSize(new Dimension(190, 150));

		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				leftSpeedLabel.setText(""+controller.getLeftSpeed());
				rightSpeedLabel.setText(""+controller.getRightSpeed());
			}
		});
		
		SixaxisInput.getInstance().registerDirectionButtons(forwardButton, backButton, leftButton, rightButton);
	}
	
	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.MOVEMENT;
	}

}
