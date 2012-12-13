package raisa.ui.measurements;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;

/* package */ class SampleCounterPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private JLabel sentField;
	private JLabel receivedField;
	private int counter = 0;
	
	public SampleCounterPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 60));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());
		TitledBorder border = new TitledBorder("Sample counters");
		setBorder(border);
		setLayout(new GridLayout(2, 1));
		sentField = new JLabel("Sent: -");
		sentField.setAlignmentX(LEFT_ALIGNMENT);
		sentField.setToolTipText("Message number from the latest sample. Set by the sender.");
		this.add(sentField);
		receivedField = new JLabel("Recv: 0");
		receivedField.setAlignmentX(LEFT_ALIGNMENT);
		receivedField.setToolTipText("Number of received samples");
		this.add(receivedField);
	}
	
	@Override
	public void update(Sample sample) {
		counter ++;
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
