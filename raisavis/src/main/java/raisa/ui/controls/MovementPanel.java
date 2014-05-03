package raisa.ui.controls;

import java.awt.Component;
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
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@SuppressWarnings(value = { "SE_BAD_FIELD", "SIC_INNER_SHOULD_BE_STATIC_ANON" },
	justification="MovementPanel needs not to be serializable and non-static action listeners are not a problem here")
public class MovementPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;

	private final VisualizerFrame frame;
	private final WorldModel world;
	private final JPanel controllerOptions = new JPanel();
	private final JPanel manualControl = new JPanel();
	private final JPanel pidControl = new JPanel();

	public MovementPanel(final VisualizerFrame frame, final WorldModel world, final BasicController basicController, final PidController pidController) {
		this.frame = frame;
		this.world = world;
		setBorder(new TitledBorder("Movement"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		createManualControlPanel(basicController);
		createPidControlPanel(pidController);

		final JComboBox<String> controllerSelection = new JComboBox<>();
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

		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				leftSpeedLabel.setText(""+controller.getLeftSpeed());
				rightSpeedLabel.setText(""+controller.getRightSpeed());
			}
		});

		SixaxisInput.getInstance().registerMovementButtons(forwardButton, backButton, leftButton, rightButton, stopButton);
	}

	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.MOVEMENT;
	}

}
