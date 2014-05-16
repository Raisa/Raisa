package raisa.ui.measurements;

import static java.lang.System.currentTimeMillis;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;

/* package */ class SampleCounterPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private final JLabel sentField;
	private final JLabel receivedField;
	private final JLabel receivedPerSecondField;
	private int counter = 0;
	private long windowStartTime = currentTimeMillis() / 1000;
	private int samplesAfterWindowStart = 0;

	public SampleCounterPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(220, 60));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());
		TitledBorder border = new TitledBorder("Sample counters");
		setBorder(border);
		setLayout(new GridLayout(3, 1));
		sentField = new JLabel("Sent: -");
		sentField.setAlignmentX(LEFT_ALIGNMENT);
		sentField.setToolTipText("Message number from the latest sample.\nSet by the sender.");
		this.add(sentField);
		receivedField = new JLabel("Recv: 0");
		receivedField.setAlignmentX(LEFT_ALIGNMENT);
		receivedField.setToolTipText("Number of received samples");
		this.add(receivedField);
		receivedPerSecondField = new JLabel("Recv/s: 0");
		receivedPerSecondField.setAlignmentX(LEFT_ALIGNMENT);
		receivedPerSecondField.setToolTipText("Number of received samples per second");
		this.add(receivedPerSecondField);
	}

	@Override
	public void update(Sample sample) {
		long currentTime = currentTimeMillis() / 1000;
		if (currentTime != windowStartTime) {
			receivedPerSecondField.setText("Recv/s: " + samplesAfterWindowStart);
			windowStartTime = currentTime;
			samplesAfterWindowStart = 0;
		}
		samplesAfterWindowStart++;
		counter++;
		String sent = sample.getMessageNumber() > 0 ? "" + sample.getMessageNumber() : "-";
		sentField.setText("Sent: " + sent);
		receivedField.setText("Recv: " + counter);
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.SAMPLE_COUNTER;
	}

}
