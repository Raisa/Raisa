package raisa.ui.controls;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import raisa.comms.BasicController;
import raisa.comms.Controller;
import raisa.comms.ControllerListener;

public class PanAndTiltSystemPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public PanAndTiltSystemPanel(final BasicController controller) {
		setBorder(new TitledBorder("Pan & tilt system"));
		JButton tiltUpButton = new JButton("U");
		tiltUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendTiltUp();
			}			
		});
		JButton centerButton = new JButton("C");
		centerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendCenterPanAndTilt();
			}			
		});
		JButton tiltDownButton = new JButton("D");
		tiltDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendTiltDown();
			}			
		});
		JButton panLeftButton = new JButton("L");
		panLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendPanLeft();
			}			
		});
		JButton panRightButton = new JButton("R");
		panRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.sendPanRight();
			}			
		});
		
		setLayout(new GridLayout(3, 3));
		final JLabel panAngleLabel = new JLabel(String.valueOf(controller.getPanServoAngle()), JLabel.CENTER);
		final JLabel tiltAngleLabel = new JLabel(String.valueOf(controller.getTiltServoAngle()), JLabel.CENTER);

		add(panAngleLabel);
		add(tiltUpButton);
		add(tiltAngleLabel);
		add(panLeftButton);
		add(centerButton);
		add(panRightButton);
		add(new JSeparator());
		add(tiltDownButton);
		add(new JSeparator());
		
		setPreferredSize(new Dimension(150, 150));
		setMaximumSize(new Dimension(150, 150));

		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				panAngleLabel.setText(""+controller.getPanServoAngle());
				tiltAngleLabel.setText(""+controller.getTiltServoAngle());
			}		
		});

	}

}
