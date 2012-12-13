package raisa.ui.controls;

import javax.swing.JPanel;

/* package */ abstract class ControlSubPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private boolean isDisplayed;
	
	public boolean isDisplayed() {
		return this.isDisplayed;
	}
	
	public void setDisplayed(boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	
	public abstract ControlTypeEnum getControlSubPanelType();

}
