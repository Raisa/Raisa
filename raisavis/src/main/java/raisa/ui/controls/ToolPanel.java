package raisa.ui.controls;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import raisa.ui.UserEditUndoListener;
import raisa.ui.VisualizerFrame;

public class ToolPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;

	public ToolPanel(final VisualizerFrame frame) {
		setBorder(new TitledBorder("Map tools"));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 2));

		JToggleButton measure = new JToggleButton("Measure");
		measure.setAlignmentX(Component.CENTER_ALIGNMENT);
		measure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.selectedMeasureTool();
			}
		});
		measure.setSelected(true);
		frame.selectedMeasureTool();
		buttonPanel.add(measure);
		JToggleButton draw = new JToggleButton("Draw");
		draw.setAlignmentX(Component.CENTER_ALIGNMENT);
		draw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.selectedDrawTool();
			}
		});
		buttonPanel.add(draw);
		addToolButton(measure);
		addToolButton(draw);

		final JButton undo = new JButton("Undo");
		undo.setEnabled(false);
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.popUserEditUndoLevel();
				frame.repaint();
			}
		});
		buttonPanel.add(undo);
		final JButton redo = new JButton("Redo");
		redo.setEnabled(false);
		redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.redoUserEditUndoLevel();
				frame.repaint();
			}
		});
		buttonPanel.add(redo);
		frame.addUserEditUndoListener(new UserEditUndoListener() {
			@Override
			public void usedEditUndoAction() {
				updateUndoRedo(frame, undo, redo);
			}
		});
		add(buttonPanel);
		updateUndoRedo(frame, undo, redo);
	}

	private void updateUndoRedo(final VisualizerFrame frame, final JButton undo, final JButton redo) {
		undo.setEnabled(frame.isUserEditUndoable());
		undo.setText("Undo (" +frame.getUserUndoLevels() + ")");
		redo.setEnabled(frame.isUserEditRedoable());
		redo.setText("Redo (" + frame.getUserRedoLevels() + ")");
	}

	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.DRAWING_TOOL;
	}
}
