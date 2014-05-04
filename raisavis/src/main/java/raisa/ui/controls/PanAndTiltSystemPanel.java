package raisa.ui.controls;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import raisa.comms.ControllerListener;
import raisa.comms.controller.BasicController;
import raisa.comms.controller.Controller;
import raisa.ui.controls.sixaxis.SixaxisInput;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@SuppressWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification="Non-static action listeners are not a problem here")
public class PanAndTiltSystemPanel extends ControlSubPanel {

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

		final JLabel panAngleLabel = new JLabel(String.valueOf(controller.getPanServoAngle()), JLabel.CENTER);
		final JLabel tiltAngleLabel = new JLabel(String.valueOf(controller.getTiltServoAngle()), JLabel.CENTER);

		JPanel manualControl = new JPanel();
		manualControl.setLayout(new GridLayout(3, 3));
		manualControl.add(panAngleLabel);
		manualControl.add(tiltUpButton);
		manualControl.add(tiltAngleLabel);
		manualControl.add(panLeftButton);
		manualControl.add(centerButton);
		manualControl.add(panRightButton);
		manualControl.add(new JSeparator());
		manualControl.add(tiltDownButton);
		manualControl.add(new JSeparator());
		add(manualControl);

		controller.addContolListener(new ControllerListener() {
			@Override
			public void controlsChanged(Controller controller) {
				panAngleLabel.setText(""+controller.getPanServoAngle());
				tiltAngleLabel.setText(""+controller.getTiltServoAngle());
			}
		});

		SixaxisInput.getInstance().registerPanAndTiltButtons(tiltUpButton, tiltDownButton, panLeftButton, panRightButton, centerButton);
	}

	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.PAN_AND_TILT;
	}

}
