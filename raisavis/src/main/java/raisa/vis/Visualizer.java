package raisa.vis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

/**
 * See http://arduino.cc/playground/Interfacing/Java for RXTX library setup
 */
public class Visualizer {

	private static List<String> getExampleSamples() {
		ExampleWorld1 world = new ExampleWorld1();
		float x = 0.0f;
		float y = 0.0f;
		List<String> sampleStrings = new ArrayList<String>();
		for (int o = 0; o < 50; ++o) {
			for (int i = 0; i < 100; ++i) {
				sampleStrings.add(world.sample(x, y, (float)(i + Math.random()) / 50.0f
						* (float) Math.PI));
			}
		}
		return sampleStrings;
	}

	private static List<String> getFileSamples(String filename)
			throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(filename));
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
		return sampleStrings;
	}

	public static void main(String[] args) throws Exception {
		final VisualizerPanel visualizer = new VisualizerPanel();
		final List<String> samples = new ArrayList<String>();
		if (args.length == 0) {
			samples.addAll(getExampleSamples());
		} else {
			String inputMode = args[0];
			if ("serial".equals(inputMode)) {
				SerialWorld serialWorld = new SerialWorld(visualizer);
				serialWorld.initialize();
			} else if ("file".equals(inputMode)) {
				if (args.length != 2) {
					System.out.println("Missing filename");
				}
				String filename = args[1];
				samples.addAll(getFileSamples(filename));
			}
		}

		JFrame frame = new JFrame("Raisa Visualizer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.add(visualizer);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		visualizer.requestFocusInWindow();
		if (!samples.isEmpty()) {
			new Thread(new Runnable() {
				private int nextSample = 0;
				@Override
				public void run() {
					while (nextSample < samples.size()) {
						visualizer.update(samples.get(nextSample));
						++nextSample;
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}
				}
			}).start();
		}
	}

}
