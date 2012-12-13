package raisa.ui.measurements;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import raisa.domain.WorldModel;
import raisa.domain.robot.RobotState;
import raisa.domain.samples.Sample;

/* package */ class SpeedPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private JLabel speedMeasuredField;
	private JLabel speedEstimatedField;

	public SpeedPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 65));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());
		TitledBorder border = new TitledBorder("Track speeds (m/s)");
		setBorder(border);
		setLayout(new GridLayout(2, 1));
		speedMeasuredField = new JLabel("-, - (encoders)");
		speedMeasuredField.setAlignmentX(LEFT_ALIGNMENT);
		speedEstimatedField = new JLabel("-, - (estimated)");
		speedEstimatedField.setAlignmentX(LEFT_ALIGNMENT);
		this.add(speedMeasuredField);
		this.add(speedEstimatedField);
	}

	@Override
	public void update(Sample sample) {
		DecimalFormat format = new DecimalFormat("0.000");
		RobotState measuredState = worldModel.getLatestState().getMeasuredState();
		RobotState estimatedState = worldModel.getLatestState().getEstimatedState();
		speedMeasuredField.setText(format.format(measuredState.getSpeedLeftTrack()) + ", " + format.format(measuredState.getSpeedRightTrack()) + " (encoders)");
		speedEstimatedField.setText(format.format(estimatedState.getSpeedLeftTrack()) + ", " + format.format(estimatedState.getSpeedRightTrack()) + " (estimated)");
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.SPEED;
	}

}