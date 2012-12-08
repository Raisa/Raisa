package raisa.ui.options;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JDialog;

public class VisualizationOptionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public VisualizationOptionsDialog(Frame parent) {
		super(parent, "Visualization options", true);		
		add(new VisualizationOptionsPanel());
		
	    Toolkit tk = Toolkit.getDefaultToolkit();
	    Dimension screenSize = tk.getScreenSize();
	    int screenHeight = screenSize.height;
	    int screenWidth = screenSize.width;
	    setLocation(screenWidth / 4, screenHeight / 4);

	    setPreferredSize(new Dimension(350,300));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
	}
	
}
