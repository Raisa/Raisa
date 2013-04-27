package raisa.ui.controls;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/* package */ abstract class ControlSubPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private boolean isDisplayed;
	private static ButtonGroup toolButtonGroup = new ButtonGroup();

	protected static void addToolButton(JToggleButton toolButton) {
		toolButtonGroup.add(toolButton);
	}
	
	public boolean isDisplayed() {
		return this.isDisplayed;
	}
	
	public void setDisplayed(boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	
	public abstract ControlTypeEnum getControlSubPanelType();

}
