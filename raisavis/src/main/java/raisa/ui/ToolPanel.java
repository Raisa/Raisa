package raisa.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

public class ToolPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public ToolPanel(VisualizerFrame frame) {
		setBorder(new TitledBorder("Tools"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JToggleButton measure = new JToggleButton("Measure");
		measure.setAlignmentX(Component.CENTER_ALIGNMENT);
		measure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
			}
		});
		add(measure);
		JToggleButton draw = new JToggleButton("Draw");
		draw.setAlignmentX(Component.CENTER_ALIGNMENT);
		draw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
			}
		});
		add(draw);
		ButtonGroup toolGroup = new ButtonGroup();
		toolGroup.add(measure);
		toolGroup.add(draw);
	}

}
