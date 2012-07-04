package raisa.vis;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class VisualizerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private VisualizerPanel visualizer;

	public VisualizerFrame() {
		visualizer = new VisualizerPanel();
		JMenuBar menuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("Main");
		mainMenu.setMnemonic('m');
		JMenuItem reset = new JMenuItem("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		reset.setMnemonic('r');
		JMenuItem loadSimulation = new JMenuItem("Load simulation...");
		loadSimulation.setMnemonic('s');
		loadSimulation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadSimulation(null);
			}
		});
		JMenuItem loadData = new JMenuItem("Load data...");
		loadData.setMnemonic('d');
		loadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadData(null);
			}
		});
		JMenuItem saveAs = new JMenuItem("Save as...");
		saveAs.setMnemonic('a');
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(null);
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic('x');
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mainMenu.add(reset);
		mainMenu.addSeparator();
		mainMenu.add(loadSimulation);
		mainMenu.add(loadData);
		mainMenu.add(saveAs);
		mainMenu.addSeparator();
		mainMenu.add(exit);
		menuBar.add(mainMenu);

		JMenu viewMenu = new JMenu("View");
		mainMenu.setMnemonic('m');
		JMenuItem zoomIn = new JMenuItem("Zoom in");
		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualizer.zoomIn();
			}
		});
		zoomIn.setMnemonic('i');
		JMenuItem zoomOut = new JMenuItem("Zoom out");
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualizer.zoomOut();
			}
		});
		zoomOut.setMnemonic('o');
		
		viewMenu.add(zoomIn);
		viewMenu.add(zoomOut);
		menuBar.add(viewMenu);

		Communicator communicator = new Communicator();
		
		ControlPanel controlPanel = new ControlPanel(visualizer, communicator);
		
		JFrame frame = new JFrame("Raisa Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.getContentPane().add(visualizer, BorderLayout.CENTER);
		frame.getContentPane().add(controlPanel, BorderLayout.WEST);
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		visualizer.requestFocusInWindow();
	}

	public void save(String fileName) {
		if (fileName == null) {
			final JFileChooser chooser = new JFileChooser();
			chooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					try {
						internalSave(fileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			chooser.showSaveDialog(this);
		} else {
			try {
				internalSave(fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadSimulation(String fileName) {
		if (fileName == null) {
			final JFileChooser chooser = new JFileChooser();
			chooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					try {
						internalLoad(fileName, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			chooser.showOpenDialog(this);
		} else {
			try {
				internalLoad(fileName, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadData(String fileName) {
		if (fileName == null) {
			final JFileChooser chooser = new JFileChooser();
			chooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					try {
						internalLoad(fileName, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			chooser.showOpenDialog(this);
		} else {
			try {
				internalLoad(fileName, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void internalSave(String fileName) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		for (Sample sample : visualizer.getSamples()) {
			writer.write(sample.sampleString);
			writer.newLine();
		}
		writer.close();
	}

	private void internalLoad(String fileName, boolean delayed) throws FileNotFoundException, IOException {
		BufferedReader fr = new BufferedReader(new FileReader(fileName));
		List<String> sampleStrings = new ArrayList<String>();
		String line = fr.readLine();
		while (line != null) {
			if (!Sample.isValid(line)) {
				System.out.println("Invalid sample! \"" + line + "\"");
			} else {
				sampleStrings.add(line);
			}
			line = fr.readLine();
		}

		spawnSimulationThread(sampleStrings, delayed);
	}

	public void reset() {
		visualizer.reset();
	}

	public void exit() {
		System.exit(0);
	}

	public void spawnSampleSimulationThread(final List<Sample> samples, final boolean delayed) {
		if (!samples.isEmpty()) {
			new Thread(new Runnable() {
				private int nextSample = 0;

				@Override
				public void run() {
					while (nextSample < samples.size()) {
						visualizer.update(samples.get(nextSample));
						++nextSample;
						if (delayed) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			}).start();
		}
	}

	public void spawnSimulationThread(final List<String> samples, final boolean delayed) {
		if (!samples.isEmpty()) {
			new Thread(new Runnable() {
				private int nextSample = 0;

				@Override
				public void run() {
					while (nextSample < samples.size()) {
						visualizer.update(samples.get(nextSample));
						++nextSample;
						if (delayed) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			}).start();
		}
	}

	public VisualizerPanel getVisualizer() {
		return visualizer;
	}
}
