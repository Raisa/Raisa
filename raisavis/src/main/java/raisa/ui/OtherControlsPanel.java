package raisa.ui;

import java.awt.Component;
import java.awt.Dimension;
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

import raisa.comms.BasicController;
import raisa.comms.Controller;
import raisa.comms.ControllerListener;
import raisa.config.InputOutputTargetEnum;
import raisa.config.LocalizationModeEnum;
import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.session.SessionWriter;
import raisa.simulator.RobotSimulator;

public class OtherControlsPanel extends JPanel implements VisualizerConfigListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OtherControlsPanel.class);

	private JComboBox localizationModeBox;
	
	public OtherControlsPanel(final BasicController controller, SessionWriter sessionWriter, RobotSimulator robotSimulator) {
		setBorder(new TitledBorder("Other"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		createLightsControl(controller);
		createDataCaptureControl(sessionWriter);
		createInputOutputTargetControl();
		createLocalizationModeControl();
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}
	
	private void createDataCaptureControl(final SessionWriter sessionWriter) {
		final JToggleButton button = new JToggleButton("Data capture");
		button.addActionListener(new ActionListener() {
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

		add(button);
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
		final JToggleButton lightsButton = new JToggleButton("Lights");
		lightsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendLights();
			}
		});

		add(lightsButton);
		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				lightsButton.setSelected(controller.getLights());
			}
		});
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
	
	protected void add(JComponent component) {
		component.setAlignmentX(LEFT_ALIGNMENT);
		super.add(component);		
	}

	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (config.isChanged(VisualizerConfigItemEnum.LOCALIZATION_MODE)) {
			localizationModeBox.setSelectedIndex(config.getLocalizationMode().getIndex());
		}
	}
}
