package raisa.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import raisa.comms.Communicator;

public class CommunicatorPanel extends ControlSubPanel {
	private static final long serialVersionUID = 1L;

	private final Communicator communicator;

	public CommunicatorPanel(Communicator communicator) {
		this.communicator = communicator;

		setBorder(new TitledBorder("Communications"));
		JButton reconnect = new JButton("Reconnect");
		reconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CommunicatorPanel.this.communicator.close();
				CommunicatorPanel.this.communicator.connect();
			}
		});
		add(reconnect);
	}

	@Override
	public ControlTypeEnum getControlSubPanelType() {
		return ControlTypeEnum.COMMUNICATOR;
	}

}
