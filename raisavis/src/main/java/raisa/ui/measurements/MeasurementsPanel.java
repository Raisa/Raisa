package raisa.ui.measurements;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;
import raisa.domain.samples.SampleListener;

public class MeasurementsPanel extends JPanel implements SampleListener, VisualizerConfigListener {
	private static final long serialVersionUID = 1L;

	private List<MeasurementSubPanel> subpanels = new ArrayList<MeasurementSubPanel>();
	
	public MeasurementsPanel(WorldModel worldModel) {
		worldModel.addSampleListener(this);
		TitledBorder border = new TitledBorder("Measurements");
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.setPreferredSize(new Dimension(200, 300));
		this.setMinimumSize(getPreferredSize());
		this.setMaximumSize(getPreferredSize());
		
		subpanels.add(new HeadingPanel(worldModel));
		subpanels.add(new SpeedPanel(worldModel));
		subpanels.add(new OdometerPanel(worldModel));
		subpanels.add(new DistanceSensorStatusPanel(worldModel));
		subpanels.add(new AccelerationPanel(worldModel));
		subpanels.add(new GyroscopePanel(worldModel));
		subpanels.add(new SoundPanel(worldModel));
		subpanels.add(new SampleCounterPanel(worldModel));
		
		this.setDisplayedPanels(VisualizerConfig.getInstance());
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}

	@Override
	public void sampleAdded(Sample sample) {
		for (MeasurementSubPanel subpanel : subpanels) {
			if (subpanel.isDisplayed()) {
				subpanel.update(sample);
			}
		}
	}
	
	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (!config.isChanged(VisualizerConfigItemEnum.DISPLAYED_MEASUREMENTS)) {
			return;
		}
		setDisplayedPanels(config);
		this.validate();
		this.repaint();
	}	
	
	private void setDisplayedPanels(VisualizerConfig config) {
		Set<MeasurementTypeEnum> displayedMeasurements = config.getDisplayedMeasurements();
		for (MeasurementSubPanel subpanel : subpanels) {
			subpanel.setDisplayed(displayedMeasurements.contains(subpanel.getMeasurementSubPanelType()));
		}
		this.removeAll();
		for (MeasurementSubPanel subpanel : subpanels) {
			if (subpanel.isDisplayed()) {
				this.add(subpanel);
			}
		}
	}

}
