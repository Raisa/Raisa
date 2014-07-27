package raisa.ui.controls;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.ControllerListener;
import raisa.comms.controller.BasicController;
import raisa.comms.controller.Controller;
import raisa.config.InputOutputTargetEnum;
import raisa.config.VisualizerConfig;
import raisa.session.SessionWriter;
import raisa.simulator.RobotSimulator;
import raisa.ui.controls.sixaxis.SixaxisInput;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@SuppressWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification="Non-static action listeners are not a problem here")
public class OtherControlsPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OtherControlsPanel.class);

	final JToggleButton lightsButton = new JToggleButton("Lights");
	final JToggleButton takePictureButton = new JToggleButton("Take pic");
	final JToggleButton servosButton = new JToggleButton("Servos");
	final JToggleButton dataCaptureButton = new JToggleButton("Capture");
	final JToggleButton compassButton = new JToggleButton("Compass");

	final VisualizerConfig config;

	public OtherControlsPanel(final BasicController controller, SessionWriter sessionWriter, RobotSimulator robotSimulator) {
		config = VisualizerConfig.getInstance();
		setBorder(new TitledBorder("Other"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);

		createLightsControl(controller);
		createServosControl(controller);
		createTakePictureControl(controller);
		createDataCaptureControl(sessionWriter);
		createCompassControl();

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 2));
		buttonPanel.add(lightsButton);
		buttonPanel.add(servosButton);
		buttonPanel.add(takePictureButton);
		buttonPanel.add(dataCaptureButton);
		buttonPanel.add(compassButton);
		add(buttonPanel);

		createInputOutputTargetControl();
		SixaxisInput.getInstance().registerActionButtons(lightsButton, takePictureButton, servosButton);
	}

	private void createCompassControl() {
		compassButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config.setUseCompass(!config.getUseCompass());
			}
		});
	}

	private void createDataCaptureControl(final SessionWriter sessionWriter) {
		dataCaptureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//controller.sendLights();
				try {
					if(sessionWriter.isCapturingData()) {
						sessionWriter.close();
					} else {
						sessionWriter.start();
					}
				} catch (IOException e) {
					log.error("", e);
				}
			}
		});
	}

	private void createInputOutputTargetControl() {
		final JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new GridLayout(3, 1));
		final JLabel ioLabel = new JLabel("Input/Output:");
		final String[] targets = { "Simulation file", "Raisa actual", "Simulator" };
		final JComboBox<String> box = new JComboBox<>(targets);
		final JPanel tickPanel = new JPanel();
		tickPanel.setLayout(new GridLayout(3, 2));
		final JLabel tickLabel = new JLabel("Ticks / s");
		final JTextField ticksPerSecondField = new JTextField(Integer.toString(config.getSimulatorTicksPerSecond()), 3);
		final JLabel servoSpeedLabel = new JLabel("Servo degrees / s");
		final JTextField servoSpeedField = new JTextField(Integer.toString(config.getSimulatorServoDegreesPerSecond()), 5);
		final JLabel realtimeLabel = new JLabel("Real time");
		final JCheckBox realtimeCheckbox = new JCheckBox();
		realtimeCheckbox.setSelected(config.isSimulatorRealTime());
		tickPanel.add(tickLabel);
		tickPanel.add(ticksPerSecondField);
		tickPanel.add(servoSpeedLabel);
		tickPanel.add(servoSpeedField);
		tickPanel.add(realtimeLabel);
		tickPanel.add(realtimeCheckbox);
		box.setSelectedIndex(config.getInputOutputTarget().getIndex());
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				VisualizerConfig config = VisualizerConfig.getInstance();
				switch (box.getSelectedIndex()) {
				case 0:
					config.setInputOutputTarget(InputOutputTargetEnum.FILE_SIMULATION);
					ioPanel.remove(tickPanel);
					break;
				case 1:
					config.setInputOutputTarget(InputOutputTargetEnum.RAISA_ACTUAL);
					ioPanel.remove(tickPanel);
					break;
				default:
					config.setInputOutputTarget(InputOutputTargetEnum.REALTIME_SIMULATOR);
					ioPanel.add(tickPanel);
					break;
				}
				config.notifyVisualizerConfigListeners();
			}
		});
		ticksPerSecondField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					int tmp = Integer.parseInt(ticksPerSecondField.getText());
					if (tmp > 0) {
						config.setSimulatorTicksPerSecond(tmp);
					}
				} catch(NumberFormatException nex) {
					log.debug("Invalid samples per second: " + ticksPerSecondField.getText());
				}
			}
		});
		servoSpeedField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					int tmp = Integer.parseInt(servoSpeedField.getText());
					if (tmp > 0) {
						config.setSimulatorServoDegreesPerSecond(tmp);
					}
				} catch(NumberFormatException nex) {
					log.debug("Invalid degrees per second: " + servoSpeedField.getText());
				}
			}
		});
		realtimeCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.setSimulatorRealTime(realtimeCheckbox.isSelected());
			}
		});
		ioPanel.add(ioLabel);
		ioPanel.add(box);
		add(ioPanel);
	}

	private void createLightsControl(final BasicController controller) {
		lightsButton.setSelected(controller.getLights());
		lightsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendLights();
			}
		});
		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				lightsButton.setSelected(controller.getLights());
			}
		});
	}

	private void createServosControl(final BasicController controller) {
		servosButton.setSelected(controller.getServos());
		servosButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendServos();
			}
		});
		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				servosButton.setSelected(controller.getServos());
			}
		});
	}

	private void createTakePictureControl(final BasicController controller) {
		takePictureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendTakePicture();
				takePictureButton.setSelected(false);
			}
		});
	}

	protected void add(JComponent component) {
		component.setAlignmentX(LEFT_ALIGNMENT);
		super.add(component);
	}

	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.OTHER;
	}

}
