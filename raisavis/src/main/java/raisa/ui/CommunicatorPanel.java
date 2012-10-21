package raisa.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import raisa.comms.Communicator;

public class CommunicatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Communicator communicator;
	
	public CommunicatorPanel(Communicator communicator) {
		this.communicator = communicator;
		
		setBorder(new TitledBorder("Communications"));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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
}
