package raisa.ui.measurements;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MeasurementGraphPanel extends JPanel {
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
