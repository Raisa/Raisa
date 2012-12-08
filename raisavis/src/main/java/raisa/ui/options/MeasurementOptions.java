package raisa.ui.options;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.config.VisualizerConfig;
import raisa.ui.measurements.MeasurementTypeEnum;

class MeasurementsOptions extends JPanel {
	private static final long serialVersionUID = 1L;	
	
	public MeasurementsOptions() {
		setBorder(new TitledBorder("Measurements"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(new MeasurementCheckBox("Acceleration", MeasurementTypeEnum.ACCELERATION));
		this.add(new MeasurementCheckBox("Gyroscope", MeasurementTypeEnum.GYROSCOPE));
		this.add(new MeasurementCheckBox("Heading", MeasurementTypeEnum.HEADING));
		this.add(new MeasurementCheckBox("Odometer", MeasurementTypeEnum.ODOMETER));
		this.add(new MeasurementCheckBox("Sample counter", MeasurementTypeEnum.SAMPLE_COUNTER));
		this.add(new MeasurementCheckBox("Sound", MeasurementTypeEnum.SOUND));
		this.add(new MeasurementCheckBox("Speed", MeasurementTypeEnum.SPEED));
	}
	
	private class MeasurementCheckBox extends JCheckBox implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private MeasurementTypeEnum measurementType;
		
		public MeasurementCheckBox(String text, MeasurementTypeEnum measurementType) {
			super(text);
			this.measurementType = measurementType;
			VisualizerConfig config = VisualizerConfig.getInstance();
			this.setSelected(config.getDisplayedMeasurements().contains(measurementType));
			this.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			VisualizerConfig config = VisualizerConfig.getInstance();
			if (this.isSelected()) {
				config.addDisplayedMeasurement(measurementType);
			} else {
				config.removeDisplayedMeasurement(measurementType);
			}
			config.notifyVisualizerConfigListeners();
		}
		
	}
	
}
