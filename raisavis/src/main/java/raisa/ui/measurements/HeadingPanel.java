package raisa.ui.measurements;

import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import raisa.domain.RobotState;
import raisa.domain.Sample;
import raisa.domain.WorldModel;

/* package */ class HeadingPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel headingMeasuredField;
	private JLabel headingEstimatedField;		

	public HeadingPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 65));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());
		TitledBorder border = new TitledBorder("Heading (degrees)");
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		headingMeasuredField = new JLabel(":- (compass)");
		headingMeasuredField.setAlignmentX(LEFT_ALIGNMENT);
		this.add(headingMeasuredField);
		headingEstimatedField = new JLabel(":- (estimated)");
		headingEstimatedField.setAlignmentX(LEFT_ALIGNMENT);
		this.add(headingEstimatedField);
	}

	@Override
	public void update(Sample sample) {
		DecimalFormat format = new DecimalFormat("000");
		headingMeasuredField.setText(format.format(Math.toDegrees((sample.getCompassDirection() % 360))) + " (compass)");
		
		RobotState estimatedState = worldModel.getLatestState().getEstimatedState();
		int estimatedHeading = (int)Math.toDegrees((estimatedState.getHeading() % 360));
		if (estimatedHeading < 0) {
			estimatedHeading += 360;
		}
		headingEstimatedField.setText(format.format(estimatedHeading) + " (estimated)");
		repaint();
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.HEADING;
	}

}