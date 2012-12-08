package raisa.ui.measurements;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

import raisa.domain.Sample;
import raisa.domain.WorldModel;
import raisa.util.Vector3D;

/* package */ class AccelerationPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private MeasurementGraphPanel accXPanel;
	private MeasurementGraphPanel accYPanel;
	private MeasurementGraphPanel accZPanel;

	public AccelerationPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 150));
		this.setPreferredSize(new Dimension(190, 150));
		this.setMaximumSize(new Dimension(190, 150));
		TitledBorder border = new TitledBorder("Acceleration (m/s^2)");
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		accXPanel = new MeasurementGraphPanel(20f);
		accXPanel.setAlignmentX(LEFT_ALIGNMENT);
		accXPanel.setTextValue("X: -");
		accYPanel = new MeasurementGraphPanel(20f);
		accYPanel.setAlignmentX(LEFT_ALIGNMENT);
		accYPanel.setTextValue("Y: -");
		accZPanel = new MeasurementGraphPanel(20f);
		accZPanel.setAlignmentX(LEFT_ALIGNMENT);
		accZPanel.setTextValue("Z: -");
		this.add(accXPanel);
		this.add(accYPanel);
		this.add(accZPanel);
	}

	@Override
	public void update(Sample sample) {
		DecimalFormat format = new DecimalFormat("0.000");
		Vector3D acceleration = sample.getAcceleration();
		accXPanel.setTextValue("X: " + format.format(acceleration.getX()));
		accYPanel.setTextValue("Y: " + format.format(acceleration.getY()));
		accZPanel.setTextValue("Z: " + format.format(acceleration.getZ()));
		List<Sample> samples = worldModel.getLastSamples(90);
		List<Float> samplesX = new LinkedList<Float>();
		List<Float> samplesY = new LinkedList<Float>();
		List<Float> samplesZ = new LinkedList<Float>();
		for (Sample s : samples) {
			samplesX.add(s.getAcceleration().getX());
			samplesY.add(s.getAcceleration().getY());
			samplesZ.add(s.getAcceleration().getZ());
		}
		accXPanel.setMeasurements(samplesX);
		accYPanel.setMeasurements(samplesY);
		accZPanel.setMeasurements(samplesZ);
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.ACCELERATION;
	}

}
