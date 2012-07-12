package raisa.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.BasicController;
import raisa.comms.Controller;
import raisa.comms.ControllerListener;
import raisa.session.SessionWriter;

public class OtherControlsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OtherControlsPanel.class);

	public OtherControlsPanel(final BasicController controller, SessionWriter sessionWriter) {
		setBorder(new TitledBorder("Other"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		createLightsControl(controller);
		createDataCaptureControl(sessionWriter);
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
}
