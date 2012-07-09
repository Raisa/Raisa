package raisa.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import raisa.comms.BasicController;
import raisa.comms.ConsoleCommunicator;
import raisa.comms.FailoverCommunicator;
import raisa.comms.SampleParser;
import raisa.comms.SerialCommunicator;
import raisa.domain.Sample;
import raisa.domain.WorldModel;
import raisa.ui.tool.DrawTool;
import raisa.ui.tool.MeasureTool;
import raisa.ui.tool.Tool;

public class VisualizerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private VisualizerPanel visualizerPanel;
	private File defaultDirectory = null;
	private final WorldModel worldModel;
	private Tool currentTool;
	private DrawTool drawTool = new DrawTool(this);
	private MeasureTool measureTool = new MeasureTool(this);
	
	public VisualizerFrame(WorldModel worldModel) {
		visualizerPanel = new VisualizerPanel(this, worldModel);
		MeasurementsPanel measurementsPanel = new MeasurementsPanel(worldModel);
		this.worldModel = worldModel;
		JMenuBar menuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("Main");
		mainMenu.setMnemonic('m');
		JMenuItem reset = new JMenuItem("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				repaint();
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
				visualizerPanel.zoomIn();
				updateTitle();
			}
		});
		zoomIn.setMnemonic('i');
		JMenuItem zoomOut = new JMenuItem("Zoom out");
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualizerPanel.zoomOut();
				updateTitle();
			}
		});
		zoomOut.setMnemonic('o');
		
		viewMenu.add(zoomIn);
		viewMenu.add(zoomOut);
		menuBar.add(viewMenu);

		FailoverCommunicator communicator = new FailoverCommunicator(new SerialCommunicator(worldModel), new ConsoleCommunicator());;
		communicator.connect();

		final BasicController controller = new BasicController(communicator);
		
		setCurrentTool(drawTool);
		
		ControlPanel controlPanel = new ControlPanel(this, visualizerPanel, controller, communicator);
		
		int nextFreeActionKey = 0;
		final int ZOOM_IN_ACTION_KEY = ++nextFreeActionKey;
		final int ZOOM_OUT_ACTION_KEY = ++nextFreeActionKey;
		final int CLEAR_HISTORY_ACTION_KEY = ++nextFreeActionKey;
		final int LIMIT_HISTORY_ACTION_KEY = ++nextFreeActionKey;
		final int STOP_ACTION_KEY = ++nextFreeActionKey;
		final int LEFT_ACTION_KEY = ++nextFreeActionKey;
		final int RIGHT_ACTION_KEY = ++nextFreeActionKey;
		final int FORWARD_ACTION_KEY = ++nextFreeActionKey;
		final int BACK_ACTION_KEY = ++nextFreeActionKey;
		final int LIGHTS_ACTION_KEY = ++nextFreeActionKey;		
		
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('+'), ZOOM_IN_ACTION_KEY);
		visualizerPanel.getActionMap().put(ZOOM_IN_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				visualizerPanel.zoomIn();
				updateTitle();
			}
		});
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('-'), ZOOM_OUT_ACTION_KEY);
		visualizerPanel.getActionMap().put(ZOOM_OUT_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				visualizerPanel.zoomOut();
				updateTitle();
			}
		});
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('c'), CLEAR_HISTORY_ACTION_KEY);
		visualizerPanel.getActionMap().put(CLEAR_HISTORY_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				visualizerPanel.clear();
			}
		});
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('h'), LIMIT_HISTORY_ACTION_KEY);
		visualizerPanel.getActionMap().put(LIMIT_HISTORY_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				visualizerPanel.removeOldSamples();
			}
		});
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), LEFT_ACTION_KEY);
		visualizerPanel.getActionMap().put(LEFT_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				controller.sendLeft();
			}
		});
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), RIGHT_ACTION_KEY);
		visualizerPanel.getActionMap().put(RIGHT_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				controller.sendRight();
			}
		});
		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), STOP_ACTION_KEY);
		visualizerPanel.getActionMap().put(STOP_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				controller.sendStop();
			}
		});

		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), FORWARD_ACTION_KEY);
		visualizerPanel.getActionMap().put(FORWARD_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				controller.sendForward();
			}
		});

		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), BACK_ACTION_KEY);
		visualizerPanel.getActionMap().put(BACK_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				controller.sendBack();
			}
		});

		visualizerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('l'), LIGHTS_ACTION_KEY);
		visualizerPanel.getActionMap().put(LIGHTS_ACTION_KEY, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				controller.sendLights();
			}
		});
		

		updateTitle();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 400);
		getContentPane().add(visualizerPanel, BorderLayout.CENTER);
		getContentPane().add(controlPanel, BorderLayout.WEST);
		getContentPane().add(measurementsPanel, BorderLayout.EAST);
		setJMenuBar(menuBar);
		setVisible(true);
		setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void setCurrentTool(Tool tool) {
		this.currentTool = tool;
	}

	public void save(String fileName) {
		if (fileName == null) {
			final JFileChooser chooser = new JFileChooser(defaultDirectory);
			chooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					try {
						saveDefaultDirectory(fileName);
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
			final JFileChooser chooser = new JFileChooser(defaultDirectory);
			chooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					try {
						saveDefaultDirectory(fileName);
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
			final JFileChooser chooser = new JFileChooser(defaultDirectory);
			chooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					try {
						saveDefaultDirectory(fileName);
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
		for (Sample sample : worldModel.getSamples()) {
			writer.write(sample.getSampleString());
			writer.newLine();
		}
		writer.close();
	}

	private void internalLoad(String fileName, boolean delayed) throws FileNotFoundException, IOException {
		BufferedReader fr = new BufferedReader(new FileReader(fileName));
		List<String> sampleStrings = new ArrayList<String>();
		String line = fr.readLine();
		SampleParser parser = new SampleParser();
		while (line != null) {
			if (!parser.isValid(line)) {
				System.out.println("Invalid sample! \"" + line + "\"");
			} else {
				sampleStrings.add(line);
			}
			line = fr.readLine();
		}

		spawnSimulationThread(sampleStrings, delayed);
	}

	public void reset() {
		visualizerPanel.reset();
		updateTitle();
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
						worldModel.addSample(samples.get(nextSample));
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
						worldModel.addSample(samples.get(nextSample));
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
		return visualizerPanel;
	}
	
	private void saveDefaultDirectory(String filename) {
		defaultDirectory = new File(filename).getParentFile();
	}
	
	private void updateTitle() {
		setTitle("Raisa Visualizer - " + Math.round(visualizerPanel.getScale() * 100.0f) + "%"); 		
	}
	
	public void selectedMeasureTool() {
		setCurrentTool(measureTool);
	}

	public void selectedDrawTool() {
		setCurrentTool(drawTool);
	}

	public Tool getCurrentTool() {
		return currentTool;
	}

	public float getScale() {
		return visualizerPanel.getScale();
	}

	public void panCameraBy(float dx, float dy) {
		visualizerPanel.panCameraBy(dx, dy);
	}
}
