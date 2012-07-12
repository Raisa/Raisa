package raisa.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.domain.Robot;
import raisa.domain.Sample;
import raisa.domain.SampleListener;
import raisa.domain.WorldModel;
import raisa.util.Vector3D;

public class MeasurementsPanel extends JPanel implements SampleListener {
	private static final long serialVersionUID = 1L;

	private SpeedPanel speedPanel; 	
	private AccelerationPanel accelerationPanel; 
	private GyroscopePanel gyroscopePanel; 
	private SoundPanel soundPanel;
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
		speedPanel = new SpeedPanel(worldModel);
		this.add(speedPanel);						
		accelerationPanel = new AccelerationPanel(worldModel);
		this.add(accelerationPanel);
		gyroscopePanel = new GyroscopePanel(worldModel);
		this.add(gyroscopePanel);	
		soundPanel = new SoundPanel(worldModel);
		this.add(soundPanel);				
	}
	
	public void sampleAdded(Sample sample) {
		speedPanel.update(sample);
		accelerationPanel.update(sample);
		gyroscopePanel.update(sample);
		soundPanel.update(sample);
	}
	
	private class AccelerationPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel accXField;
		private JLabel accYField;
		private JLabel accZField;
		private MeasurementGraphPanel accXPanel;
		private MeasurementGraphPanel accYPanel;
		private MeasurementGraphPanel accZPanel;

		public AccelerationPanel(WorldModel worldModel) {
			this.setMinimumSize(new Dimension(190, 230));
			this.setPreferredSize(new Dimension(190, 230));
			this.setMaximumSize(new Dimension(190, 230));
			TitledBorder border = new TitledBorder("Acceleration (m/s^2)");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			accXField = new JLabel("X: -");
			accXField.setAlignmentX(LEFT_ALIGNMENT);
			accYField = new JLabel("Y: -");
			accYField.setAlignmentX(LEFT_ALIGNMENT);
			accZField = new JLabel("Z: -");
			accZField.setAlignmentX(LEFT_ALIGNMENT);
			accXPanel = new MeasurementGraphPanel(20f);
			accXPanel.setAlignmentX(LEFT_ALIGNMENT);
			accYPanel = new MeasurementGraphPanel(20f);
			accYPanel.setAlignmentX(LEFT_ALIGNMENT);
			accZPanel = new MeasurementGraphPanel(20f);	
			accZPanel.setAlignmentX(LEFT_ALIGNMENT);
			this.add(accXField);		
			this.add(accXPanel);
			this.add(accYField);	
			this.add(accYPanel);	
			this.add(accZField);
			this.add(accZPanel);	
		}
			
		public void update(Sample sample) {
			DecimalFormat format = new DecimalFormat("0.000");			
			Vector3D acceleration = sample.getAcceleration();
			accXField.setText("X: " + format.format(acceleration.getX()));
			accYField.setText("Y: " + format.format(acceleration.getY()));
			accZField.setText("Z: " + format.format(acceleration.getZ()));
			List<Sample> samples = worldModel.getLastSamples(120);
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

		private JLabel xField;
		private JLabel yField;
		private JLabel zField;
		private MeasurementGraphPanel xPanel;
		private MeasurementGraphPanel yPanel;
		private MeasurementGraphPanel zPanel;

		public GyroscopePanel(WorldModel worldModel) {
			this.setMinimumSize(new Dimension(190, 230));
			this.setPreferredSize(new Dimension(190, 230));
			this.setMaximumSize(new Dimension(190, 230));
			TitledBorder border = new TitledBorder("Gyroscope (dps)");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			xField = new JLabel("X: -");
			xField.setAlignmentX(LEFT_ALIGNMENT);
			yField = new JLabel("Y: -");
			yField.setAlignmentX(LEFT_ALIGNMENT);
			zField = new JLabel("Z: -");
			zField.setAlignmentX(LEFT_ALIGNMENT);
			xPanel = new MeasurementGraphPanel(20f);
			xPanel.setAlignmentX(LEFT_ALIGNMENT);
			yPanel = new MeasurementGraphPanel(20f);
			yPanel.setAlignmentX(LEFT_ALIGNMENT);
			zPanel = new MeasurementGraphPanel(20f);	
			zPanel.setAlignmentX(LEFT_ALIGNMENT);
			this.add(xField);		
			this.add(xPanel);
			this.add(yField);	
			this.add(yPanel);	
			this.add(zField);
			this.add(zPanel);	
		}
			
		public void update(Sample sample) {
			DecimalFormat format = new DecimalFormat("0.000");			
			Vector3D latestRotation = sample.getGyro();
			xField.setText("X: " + format.format(latestRotation.getX()));
			yField.setText("Y: " + format.format(latestRotation.getY()));
			zField.setText("Z: " + format.format(latestRotation.getZ()));
			List<Sample> samples = worldModel.getLastSamples(120);
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

		private JLabel soundField;
		private MeasurementGraphPanel soundGraphPanel;

		public SoundPanel(WorldModel worldModel) {
			this.setMinimumSize(new Dimension(190, 90));
			this.setPreferredSize(new Dimension(190, 90));
			this.setMaximumSize(new Dimension(190, 90));
			TitledBorder border = new TitledBorder("Sound intensity");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			soundField = new JLabel("Value: -");
			soundField.setAlignmentX(LEFT_ALIGNMENT);
			soundGraphPanel = new MeasurementGraphPanel(-0.3f);	
			soundGraphPanel.setAlignmentX(LEFT_ALIGNMENT);
			this.add(soundField);		
			this.add(soundGraphPanel);
		}
			
		public void update(Sample sample) {
			DecimalFormat format = new DecimalFormat("0000");			
			soundField.setText("Value: " + format.format(sample.getSoundIntensity()));
			List<Sample> samples = worldModel.getLastSamples(120);
			List<Float> soundIntensities = new LinkedList<Float>();
			for (Sample s : samples) {
				soundIntensities.add((float)s.getSoundIntensity());
			}
			soundGraphPanel.setMeasurements(soundIntensities);
			repaint();
		}
		
	}		
	
	private final static class MeasurementGraphPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private List<Float> measurements = new LinkedList<Float>();
		private float scaleFactor;
		
		public MeasurementGraphPanel(float scaleFactor) {
			this.setPreferredSize(new Dimension(190, 50));
			this.setMaximumSize(new Dimension(190, 50));
			this.scaleFactor = scaleFactor;
		}
		
	    public Dimension getPreferredSize() {
	        return new Dimension(190,50);
	    }
	    
	    public void setMeasurements(List<Float> measurements) {
	    	this.measurements = measurements;
	    }

	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);   
	        for (int i=0; i<measurements.size(); i++) {
	        	g.drawLine(20 + i, 25, 20 + i, 25 + (int)(scaleFactor * measurements.get(i).floatValue()));
	        }
	    }  
	    
	}

	private class SpeedPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel speedLeftTrackField;
		private JLabel speedRightTrackField;		

		public SpeedPanel(WorldModel worldModel) {
			this.setMinimumSize(new Dimension(190, 70));
			this.setPreferredSize(new Dimension(190, 70));
			this.setMaximumSize(new Dimension(190, 70));
			TitledBorder border = new TitledBorder("Track speeds (m/s)");
			setBorder(border);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			speedLeftTrackField = new JLabel("Left: -");
			speedLeftTrackField.setAlignmentX(LEFT_ALIGNMENT);
			speedRightTrackField = new JLabel("Right: -");
			speedRightTrackField.setAlignmentX(LEFT_ALIGNMENT);
			this.add(speedLeftTrackField);	
			this.add(speedRightTrackField);					
		}
			
		public void update(Sample sample) {
			DecimalFormat format = new DecimalFormat("0.000");			
			Robot robot = worldModel.getLatestState();
			speedLeftTrackField.setText("Left: " + format.format(robot.getSpeedLeftTrack()));
			speedRightTrackField.setText("Right: " + format.format(robot.getSpeedRightTrack()));
			repaint();
		}
		
	}		
	
}
