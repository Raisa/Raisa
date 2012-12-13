package raisa.ui.measurements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;

public class DistanceSensorStatusPanel extends MeasurementSubPanel {
	private static final long serialVersionUID = 1L;

	private SingleDistanceSensorStatusPanel irSensor1Status = new SingleDistanceSensorStatusPanel("IR1");
	private SingleDistanceSensorStatusPanel irSensor2Status = new SingleDistanceSensorStatusPanel("IR2");
	private SingleDistanceSensorStatusPanel ultrasonicSensor1Status = new SingleDistanceSensorStatusPanel("US1");
	private SingleDistanceSensorStatusPanel ultrasonicSensor2Status = new SingleDistanceSensorStatusPanel("US2");
	
	public DistanceSensorStatusPanel(WorldModel worldModel) {
		super(worldModel);
		TitledBorder border = new TitledBorder("Distance sensor status");
		setBorder(border);
		this.setLayout(new GridLayout(2,2));
		this.setMinimumSize(new Dimension(190, 70));
		this.setPreferredSize(getMinimumSize());
		this.setMaximumSize(getMinimumSize());

		this.add(irSensor1Status);
		this.add(ultrasonicSensor1Status);
		this.add(irSensor2Status);
		this.add(ultrasonicSensor2Status);		
	}

	@Override
	protected MeasurementTypeEnum getMeasurementSubPanelType() {
		return MeasurementTypeEnum.DISTANCE_SENSOR_STATUS;
	}

	@Override
	protected void update(Sample sample) {
		irSensor1Status.setStatusOk(sample.isInfrared1MeasurementValid());
		irSensor2Status.setStatusOk(sample.isInfrared2MeasurementValid());
		ultrasonicSensor1Status.setStatusOk(sample.isUltrasound1MeasurementValid());
		ultrasonicSensor2Status.setStatusOk(sample.isUltrasound2MeasurementValid());
		repaint();
	}

	private class SingleDistanceSensorStatusPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private Boolean statusOk;
		private String text;
		
		public SingleDistanceSensorStatusPanel(String text) {
			super();
			this.text = text;
			this.setPreferredSize(new Dimension(75, 30));
			this.setMinimumSize(getPreferredSize());
			this.setMaximumSize(getPreferredSize());
		}
		
		public void setStatusOk(boolean statusOk) {
			this.statusOk = statusOk;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (statusOk != null) {
				if (statusOk) {
					g.setColor(new Color(0,180,0));
					g.fillRect(0, 0, getWidth(), getHeight());
				} else {
					g.setColor(new Color(180,0,0));
					g.fillRect(0, 0, getWidth(), getHeight());
				}
			}
			g.setColor(Color.BLACK);
			g.drawString(text, 25, 15);
		}
		
	}
	
}
