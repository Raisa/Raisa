package raisa.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import raisa.comms.BasicController;
import raisa.comms.Controller;
import raisa.comms.ControllerListener;

public class OtherControlsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public OtherControlsPanel(final BasicController controller) {
		setBorder(new TitledBorder("Other"));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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
