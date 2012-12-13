package raisa.ui.measurements;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import raisa.domain.WorldModel;
import raisa.domain.robot.Robot;
import raisa.domain.samples.Sample;

/* package */ class OdometerPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel measuredDistance;
	private JLabel estimatedDistance;
	
	public OdometerPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 60));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());
		TitledBorder border = new TitledBorder("Odometer (m)");
		setBorder(border);
		setLayout(new GridLayout(2, 1));
		measuredDistance = new JLabel("- (encoders)");
		measuredDistance.setAlignmentX(LEFT_ALIGNMENT);
		this.add(measuredDistance);
		estimatedDistance = new JLabel("- (estimated)");
		estimatedDistance.setAlignmentX(LEFT_ALIGNMENT);
		this.add(estimatedDistance);
	}
	
	@Override
	public void update(Sample sample) {
		DecimalFormat format = new DecimalFormat("###.00");
		Robot lastRobot = worldModel.getLatestState();
		measuredDistance.setText(format.format(lastRobot.getMeasuredState().getOdometer() / 100.0f) + " (encoders)");
		estimatedDistance.setText(format.format(lastRobot.getEstimatedState().getOdometer() / 100.0f) + " (estimated)");
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.ODOMETER;
	}
	
}