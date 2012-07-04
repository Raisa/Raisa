package raisa.vis;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class MovementPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Communicator communicator;
	
	public MovementPanel(final Communicator communicator) {
		this.communicator = communicator;

		JButton forwardButton = new JButton("F");
		forwardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				communicator.sendForward();
			}			
		});
		JButton stopButton = new JButton("S");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				communicator.sendStop();
			}			
		});
		JButton backButton = new JButton("B");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				communicator.sendBack();
			}			
		});
		JButton leftButton = new JButton("L");
		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				communicator.sendLeft();
			}			
		});
		JButton rightButton = new JButton("R");
		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				communicator.sendRight();
			}			
		});
		
		setLayout(new GridLayout(3, 3));
		add(new JSeparator());
		add(forwardButton);
		add(new JSeparator());
		add(leftButton);
		add(stopButton);
		add(rightButton);
		add(new JSeparator());
		add(backButton);
		add(new JSeparator());
		
		setPreferredSize(new Dimension(150, 150));
		setMaximumSize(new Dimension(150, 150));
	}

}
