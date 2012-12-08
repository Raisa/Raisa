package raisa.ui.measurements;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

import raisa.domain.Sample;
import raisa.domain.WorldModel;

/* package */ class SoundPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private MeasurementGraphPanel soundGraphPanel;

	public SoundPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 70));
		this.setPreferredSize(new Dimension(190, 70));
		this.setMaximumSize(new Dimension(190, 70));
		TitledBorder border = new TitledBorder("Sound intensity");
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		soundGraphPanel = new MeasurementGraphPanel(-0.3f);
		soundGraphPanel.setTextValue("Value: -");
		soundGraphPanel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(soundGraphPanel);
	}

	@Override
	public void update(Sample sample) {
		DecimalFormat format = new DecimalFormat("000");
		soundGraphPanel.setTextValue("Value: " + format.format(sample.getSoundIntensity()));
		List<Sample> samples = worldModel.getLastSamples(90);
		List<Float> soundIntensities = new LinkedList<Float>();
		for (Sample s : samples) {
			soundIntensities.add((float) s.getSoundIntensity());
		}
		soundGraphPanel.setMeasurements(soundIntensities);
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.SOUND;
	}

}
