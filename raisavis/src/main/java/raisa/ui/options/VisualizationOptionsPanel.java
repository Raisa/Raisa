package raisa.ui.options;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class VisualizationOptionsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public VisualizationOptionsPanel() {	
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setAlignmentY(Component.TOP_ALIGNMENT);
		
		add(new ControlPanelOptions());
		add(new MapAreaOptions());
		add(new MeasurementsOptions());
	}
	
}


