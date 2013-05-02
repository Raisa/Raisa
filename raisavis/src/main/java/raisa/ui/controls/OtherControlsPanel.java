package raisa.ui.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

public class OtherControlsPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OtherControlsPanel.class);
	
	final JToggleButton lightsButton = new JToggleButton("Lights");
	final JToggleButton takePictureButton = new JToggleButton("Take pic");
	final JToggleButton servosButton = new JToggleButton("Servos");
	final JToggleButton dataCaptureButton = new JToggleButton("Capture");
	final JToggleButton compassButton = new JToggleButton("Compass");

	public OtherControlsPanel(final BasicController controller, SessionWriter sessionWriter, RobotSimulator robotSimulator) {
		setBorder(new TitledBorder("Other"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setPreferredSize(new Dimension(190, 170));
		setMaximumSize(getPreferredSize());
		
		createLightsControl(controller);
		createServosControl(controller);
		createTakePictureControl(controller);
		createDataCaptureControl(sessionWriter);
		createCompassControl();

		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(180, 80));
		buttonPanel.setMaximumSize(buttonPanel.getPreferredSize());
		buttonPanel.setLayout(new GridLayout(3, 2));
		buttonPanel.add(lightsButton);
		buttonPanel.add(servosButton);
		buttonPanel.add(takePictureButton);
		buttonPanel.add(dataCaptureButton);
		buttonPanel.add(compassButton);
		add(buttonPanel);
		
		createInputOutputTargetControl();
	}
	
	private void createCompassControl() {
		compassButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				VisualizerConfig config = VisualizerConfig.getInstance();
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
		final JLabel label = new JLabel("Input/Output:");
		final String[] targets = { "Simulation file", "Raisa actual", "Simulator" };
		final JComboBox box = new JComboBox(targets);		
		final VisualizerConfig config = VisualizerConfig.getInstance();
		box.setMaximumSize(new Dimension(150,50));
		box.setSelectedIndex(config.getInputOutputTarget().getIndex());
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				VisualizerConfig config = VisualizerConfig.getInstance();				
				switch (box.getSelectedIndex()) {
				case 0:
					config.setInputOutputTarget(InputOutputTargetEnum.FILE_SIMULATION);
					break;
				case 1:
					config.setInputOutputTarget(InputOutputTargetEnum.RAISA_ACTUAL);
					break;
				default:
					config.setInputOutputTarget(InputOutputTargetEnum.REALTIME_SIMULATOR);
					break;	
				}
				config.notifyVisualizerConfigListeners();
			}			
		});
		add(label);
		add(box);
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
		takePictureButton.setPreferredSize(new Dimension(10,10));
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
