package raisa.vis;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

public class OtherControlsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public OtherControlsPanel(BasicController controller) {
		setBorder(new TitledBorder("Other"));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final JToggleButton lightsButton = new JToggleButton("Lights");
		add(lightsButton);
		controller.addContolListener(new ControlListener() {
			@Override
			public void controlsChanged(BasicController basicController) {
				lightsButton.setSelected(basicController.getLights());
			}			
		});
	}
}
