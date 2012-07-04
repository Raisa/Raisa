package raisa.vis;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class MovementPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Communicator communicator;
	
	public MovementPanel(Communicator communicator) {
		this.communicator = communicator;

		JButton forwardButton = new JButton("F");
		JButton backButton = new JButton("B");
		JButton leftButton = new JButton("L");
		JButton rightButton = new JButton("R");
		
		setLayout(new GridLayout(3, 3));
		add(new JSeparator());
		add(forwardButton);
		add(new JSeparator());
		add(leftButton);
		add(new JSeparator());
		add(rightButton);
		add(new JSeparator());
		add(backButton);
		add(new JSeparator());
		
		setPreferredSize(new Dimension(150, 150));
		setMaximumSize(new Dimension(150, 150));
	}

}
