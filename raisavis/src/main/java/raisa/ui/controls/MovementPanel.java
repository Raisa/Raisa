package raisa.ui.controls;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import raisa.comms.ControllerListener;
import raisa.comms.controller.BasicController;
import raisa.comms.controller.Controller;

public class MovementPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;

	public MovementPanel(final BasicController controller) {
		setBorder(new TitledBorder("Movement"));
		JButton forwardButton = new JButton("F");
		forwardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendForward();
			}			
		});
		JButton stopButton = new JButton("S");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendStop();
			}			
		});
		JButton backButton = new JButton("B");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendBack();
			}			
		});
		JButton leftButton = new JButton("L");
		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendLeft();
			}			
		});
		JButton rightButton = new JButton("R");
		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendRight();
			}			
		});
		
		setLayout(new GridLayout(3, 3));
		final JLabel leftSpeedLabel = new JLabel("0", JLabel.CENTER);
		final JLabel rightSpeedLabel = new JLabel("0", JLabel.CENTER);
		add(leftSpeedLabel);
		add(forwardButton);
		add(rightSpeedLabel);
		add(leftButton);
		add(stopButton);
		add(rightButton);
		add(new JSeparator());
		add(backButton);
		add(new JSeparator());
		
		setPreferredSize(new Dimension(150, 150));
		setMaximumSize(new Dimension(150, 150));
		
		controller.addContolListener(new ControllerListener() {

			@Override
			public void controlsChanged(Controller controller) {
				leftSpeedLabel.setText(""+controller.getLeftSpeed());
				rightSpeedLabel.setText(""+controller.getRightSpeed());
			}
			
		});
	}
	
	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.MOVEMENT;
	}

}
