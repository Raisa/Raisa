package raisa.ui.measurements;

import javax.swing.JPanel;

import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;

/* package */ abstract class MeasurementSubPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected WorldModel worldModel;
	protected MeasurementTypeEnum panelType;
	protected boolean isDisplayed;
	
	public MeasurementSubPanel(WorldModel worldModel) {
		super();
		this.worldModel = worldModel;
	}
		
	public boolean isDisplayed() {
		return this.isDisplayed;
	}
	
	public void setDisplayed(boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}
	
	protected abstract MeasurementTypeEnum getMeasurementSubPanelType();
	
	protected abstract void update(Sample sample);
		
}
