package raisa.ui.measurements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;

/**
 * Display compass measurement and estimated heading.  
 */
/* package */ class HeadingPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(HeadingPanel.class);
	private JLabel headingMeasuredField;
	private JLabel headingEstimatedField;		
	private HeadingArrowDisplayPanel arrowDisplayPanel;
	private static final Color estimationColor = Color.blue;
	private static final Color measurmentColor = new Color(10, 200, 10);
	
	public HeadingPanel(WorldModel worldModel) {
		super(worldModel);
		this.setMinimumSize(new Dimension(190, 190));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());
		TitledBorder border = new TitledBorder("Heading (degrees)");
		setBorder(border);
		this.setLayout(new BorderLayout());
		arrowDisplayPanel = new HeadingArrowDisplayPanel();
		this.add(arrowDisplayPanel, BorderLayout.CENTER);
		
		Panel valuesContiner = new Panel();
		this.add(valuesContiner, BorderLayout.SOUTH);
		valuesContiner.setLayout(new GridLayout(1, 2));

		headingMeasuredField = new JLabel("Comp: -");
		headingMeasuredField.setAlignmentX(LEFT_ALIGNMENT);
		headingMeasuredField.setToolTipText("Compass");
		headingMeasuredField.setForeground(measurmentColor);
		valuesContiner.add(headingMeasuredField);
		
		headingEstimatedField = new JLabel("Est: -");
		headingEstimatedField.setToolTipText("Estimated");
		headingEstimatedField.setAlignmentX(RIGHT_ALIGNMENT);
		headingEstimatedField.setForeground(estimationColor);
		valuesContiner.add(headingEstimatedField);
	}

	@Override
	public void update(Sample sample) {
		double compassHeading = Math.toDegrees(sample.getCompassDirection()) % 360;
		double estimatedHeading = Math.toDegrees(worldModel.getLatestState().getEstimatedState().getHeading()) % 360;
		if (estimatedHeading < 0) {
			estimatedHeading += 360;
		}
		updateLabels(compassHeading, estimatedHeading);
		arrowDisplayPanel.update(compassHeading, estimatedHeading);
		repaint();
	}

	private void updateLabels(double compassHeading, double estimatedHeading) {
		DecimalFormat format = new DecimalFormat("000");
		headingMeasuredField.setText("Comp: " + format.format(compassHeading));
		headingEstimatedField.setText("Est: " + format.format(estimatedHeading));
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.HEADING;
	}

	private static class HeadingArrowDisplayPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Double compassHeading = null;
		private Double estimatedHeading = null;  
		public HeadingArrowDisplayPanel() {
			setMinimumSize(new Dimension(195, 195));
		}
		public void update(double compassHeading, double estimatedHeading) {
			this.compassHeading = compassHeading;
			this.estimatedHeading = estimatedHeading;
			repaint();
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int radius = Math.min(getWidth(), getHeight()) / 2;
			
			int xOffset = 10;
			int yOffset = 0;
			int xCenter = xOffset + radius;
			int yCenter = yOffset + radius;
			drawCompassBackground(g, radius, xOffset, yOffset);

			if(compassHeading != null) {
				g.setColor(measurmentColor);
				Vector2D dir = direction(radius, compassHeading);
				g.drawLine(xCenter, yCenter, xCenter + (int)dir.getX(), yCenter - (int)dir.getY());
			}
			
			if(estimatedHeading != null) {
				g.setColor(estimationColor);
				Vector2D dir = direction(radius, estimatedHeading);
				g.drawLine(xCenter, yCenter, xCenter + (int)dir.getX(), yCenter - (int)dir.getY());				
			}
		}
		private void drawCompassBackground(Graphics g, int radius, int xOffset,	int yOffset) {
			g.clearRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.lightGray);
			g.drawOval(xOffset, yOffset, radius * 2, radius * 2);
			g.setColor(new Color(200, 200, 200));
			int centerX = xOffset + radius;
			int centerY = yOffset + radius;
			int longBar = 2 * radius / 3;
			int shortBar = radius / 2;
			g.drawLine(centerX, centerY - longBar, centerX, centerY + shortBar);
			g.drawLine(centerX + shortBar, centerY, centerX - shortBar, centerY);
		}

		private Vector2D direction(int radius, double heading) {
			double rad = Math.toRadians(-heading + 90.0);
			return new Vector2D(radius * Math.cos(rad), radius * Math.sin(rad));
		}		
	}

}