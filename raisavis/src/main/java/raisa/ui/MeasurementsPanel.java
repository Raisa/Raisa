package raisa.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.domain.Robot;
import raisa.domain.RobotState;
import raisa.domain.Sample;
import raisa.domain.SampleListener;
import raisa.domain.WorldModel;
import raisa.util.Vector3D;

public class MeasurementsPanel extends JPanel implements SampleListener {
	private static final long serialVersionUID = 1L;

	private HeadingPanel headingPanel;
	private SpeedPanel speedPanel;
	private OdometerPanel odometerPanel;
	private AccelerationPanel accelerationPanel;
	private GyroscopePanel gyroscopePanel;
	private SoundPanel soundPanel;
	private SampleCounterPanel sampleCounterPanel;
	private WorldModel worldModel;

	public MeasurementsPanel(WorldModel worldModel) {
		worldModel.addSampleListener(this);
		this.worldModel = worldModel;
		TitledBorder border = new TitledBorder("Measurements");
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(200, 300));
		this.setPreferredSize(new Dimension(200, 300));
		this.setMaximumSize(new Dimension(200, 300));
		headingPanel = new HeadingPanel(worldModel);
		this.add(headingPanel);
		speedPanel = new SpeedPanel(worldModel);
		this.add(speedPanel);
		odometerPanel = new OdometerPanel();
		this.add(odometerPanel);
		accelerationPanel = new AccelerationPanel(worldModel);
		this.add(accelerationPanel);
		gyroscopePanel = new GyroscopePanel(worldModel);
		this.add(gyroscopePanel);
		//soundPanel = new SoundPanel(worldModel);
		//this.add(soundPanel);
		this.add(sampleCounterPanel = new SampleCounterPanel() );
	}

	@Override
	public void sampleAdded(Sample sample) {
		headingPanel.update(sample);
		speedPanel.update(sample);
		odometerPanel.update(sample);
		accelerationPanel.update(sample);
		gyroscopePanel.update(sample);
		//soundPanel.update(sample);
		sampleCounterPanel.update(sample);
	}

	private class AccelerationPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private MeasurementGraphPanel accXPanel;
		private MeasurementGraphPanel accYPanel;
		private MeasurementGraphPanel accZPanel;

		public AccelerationPanel(WorldModel worldModel) {
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

	}

	private class GyroscopePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private MeasurementGraphPanel xPanel;
		private MeasurementGraphPanel yPanel;
		private MeasurementGraphPanel zPanel;

		public GyroscopePanel(WorldModel worldModel) {
			this.setMinimumSize(new Dimension(190, 150));
			this.setPreferredSize(new Dimension(190, 150));
			this.setMaximumSize(new Dimension(190, 150));
			TitledBorder border = new TitledBorder("Gyroscope (dps)");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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

	}

	private class SoundPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private MeasurementGraphPanel soundGraphPanel;

		public SoundPanel(WorldModel worldModel) {
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

	}

	private final static class MeasurementGraphPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel textField;
		private List<Float> measurements = new LinkedList<Float>();
		private float scaleFactor;

		public MeasurementGraphPanel(float scaleFactor) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			textField = new JLabel();
			textField.setAlignmentX(LEFT_ALIGNMENT);
			this.add(Box.createRigidArea(new Dimension(0, 10)));
			this.add(textField);
			this.setPreferredSize(new Dimension(190, 40));
			this.setMaximumSize(new Dimension(190, 40));
			this.scaleFactor = scaleFactor;
		}

		public void setTextValue(String text) {
			textField.setText(text);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(190, 40);
		}

		public void setMeasurements(List<Float> measurements) {
			this.measurements = measurements;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int i = 0; i < measurements.size(); i++) {
				g.drawLine(75 + i, 20, 75 + i, 20 + (int) (scaleFactor * measurements.get(i).floatValue()));
			}
		}

	}

	private class SpeedPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel speedMeasuredField;
		private JLabel speedEstimatedField;

		public SpeedPanel(WorldModel worldModel) {
			this.setMinimumSize(new Dimension(190, 65));
			this.setPreferredSize(getMinimumSize());
			this.setMaximumSize(getMinimumSize());
			TitledBorder border = new TitledBorder("Track speeds (m/s)");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			speedMeasuredField = new JLabel("-, - (encoders)");
			speedMeasuredField.setAlignmentX(LEFT_ALIGNMENT);
			speedEstimatedField = new JLabel("-, - (estimated)");
			speedEstimatedField.setAlignmentX(LEFT_ALIGNMENT);
			this.add(speedMeasuredField);
			this.add(speedEstimatedField);
		}

		public void update(Sample sample) {
			DecimalFormat format = new DecimalFormat("0.000");
			RobotState measuredState = worldModel.getLatestState().getMeasuredState();
			RobotState estimatedState = worldModel.getLatestState().getEstimatedState();
			speedMeasuredField.setText(format.format(measuredState.getSpeedLeftTrack()) + ", " + format.format(measuredState.getSpeedRightTrack()) + " (encoders)");
			speedEstimatedField.setText(format.format(estimatedState.getSpeedLeftTrack()) + ", " + format.format(estimatedState.getSpeedRightTrack()) + " (estimated)");
			repaint();
		}

	}

	private class HeadingPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel headingMeasuredField;
		private JLabel headingEstimatedField;		

		public HeadingPanel(WorldModel worldModel) {
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

	}

	private class SampleCounterPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel sentField;
		private JLabel receivedField;
		private int counter = 0;
		public SampleCounterPanel() {
			this.setMinimumSize(new Dimension(190, 60));
			this.setPreferredSize(getMinimumSize());
			this.setMaximumSize(getMinimumSize());
			TitledBorder border = new TitledBorder("Sample counters");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			sentField = new JLabel("Sent: -");
			sentField.setAlignmentX(LEFT_ALIGNMENT);
			sentField.setToolTipText("Message number from the latest sample. Set by the sender.");
			this.add(sentField);
			receivedField = new JLabel("Recv: 0");
			receivedField.setAlignmentX(LEFT_ALIGNMENT);
			receivedField.setToolTipText("Number of received samples");
			this.add(receivedField);
		}
		
		public void update(Sample sample) {
			counter ++;
			String sent = sample.getMessageNumber() > 0 ? "" + sample.getMessageNumber() : "-"; 
			sentField.setText("Sent: " + sent);
			receivedField.setText("Recv: " + counter);
			repaint();
		}
	}
	
	private class OdometerPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel measuredDistance;
		private JLabel estimatedDistance;
		private int counter = 0;
		public OdometerPanel() {
			this.setMinimumSize(new Dimension(190, 60));
			this.setPreferredSize(getMinimumSize());
			this.setMaximumSize(getMinimumSize());
			TitledBorder border = new TitledBorder("Odometer (m)");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			measuredDistance = new JLabel("- (encoders)");
			measuredDistance.setAlignmentX(LEFT_ALIGNMENT);
			this.add(measuredDistance);
			estimatedDistance = new JLabel("- (estimated)");
			estimatedDistance.setAlignmentX(LEFT_ALIGNMENT);
			this.add(estimatedDistance);
		}
		
		public void update(Sample sample) {
			DecimalFormat format = new DecimalFormat("###.00");
			Robot lastRobot = worldModel.getLatestState();
			measuredDistance.setText(format.format(lastRobot.getMeasuredState().getOdometer() / 100.0f) + " (encoders)");
			estimatedDistance.setText(format.format(lastRobot.getEstimatedState().getOdometer() / 100.0f) + " (estimated)");
			repaint();
		}
	}
	
}
