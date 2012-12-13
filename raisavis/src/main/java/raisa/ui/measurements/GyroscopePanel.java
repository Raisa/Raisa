package raisa.ui.measurements;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.border.TitledBorder;

import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;
import raisa.ui.measurements.MeasurementGraphPanel;
import raisa.util.Vector3D;

/* package */ class GyroscopePanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private MeasurementGraphPanel xPanel;
	private MeasurementGraphPanel yPanel;
	private MeasurementGraphPanel zPanel;

	public GyroscopePanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 150));
		this.setPreferredSize(new Dimension(190, 150));
		this.setMaximumSize(new Dimension(190, 150));
		TitledBorder border = new TitledBorder("Gyroscope (dps)");
		setBorder(border);
		setLayout(new GridLayout(3, 1));
		xPanel = new MeasurementGraphPanel(20f);
		xPanel.setTextValue("X: -");
		xPanel.setAlignmentX(LEFT_ALIGNMENT);
		yPanel = new MeasurementGraphPanel(20f);
		yPanel.setTextValue("Y: -");
		yPanel.setAlignmentX(LEFT_ALIGNMENT);
		zPanel = new MeasurementGraphPanel(20f);
		zPanel.setTextValue("Z: -");
		zPanel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(xPanel);
		this.add(yPanel);
		this.add(zPanel);
	}

	@Override
	public void update(Sample sample) {
		DecimalFormat format = new DecimalFormat("0.000");
		Vector3D latestRotation = sample.getGyro();
		xPanel.setTextValue("X: " + format.format(latestRotation.getX()));
		yPanel.setTextValue("Y: " + format.format(latestRotation.getY()));
		zPanel.setTextValue("Z: " + format.format(latestRotation.getZ()));
		List<Sample> samples = worldModel.getLastSamples(90);
		List<Float> samplesX = new LinkedList<Float>();
		List<Float> samplesY = new LinkedList<Float>();
		List<Float> samplesZ = new LinkedList<Float>();
		for (Sample s : samples) {
			Vector3D sampleRotation = s.getGyro();
			samplesX.add(sampleRotation.getX());
			samplesY.add(sampleRotation.getY());
			samplesZ.add(sampleRotation.getZ());
		}
		xPanel.setMeasurements(samplesX);
		yPanel.setMeasurements(samplesY);
		zPanel.setMeasurements(samplesZ);
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.GYROSCOPE;
	}

}
