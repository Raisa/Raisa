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
		add(new JToggleButton("Lights"));
	}
}
