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

	private static List<Sample> getExampleSamples() {
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
		List<Sample> samples = new ArrayList<Sample>();
		for (String str : sampleStrings) {
			samples.add(new Sample(x, y, str));
		}
		return samples;
	}

	private static List<Sample> getFileSamples(String filename)
			throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(filename));
		List<Sample> samples = new ArrayList<Sample>();
		String line = fr.readLine();
		while (line != null) {
			System.out.println(line);
			if (!line.matches("J\\d+,\\d+")) {
				System.out.println("Invalid sample!");
			} else {
				samples.add(new Sample(0, 0, line));
			}
			line = fr.readLine();
		}
		return samples;
	}

	public static void main(String[] args) throws Exception {
		VisualizerPanel visualizer = new VisualizerPanel();
		List<Spot> spots = new ArrayList<Spot>();
		if (args.length == 0) {
			List<Sample> samples = getExampleSamples();
			spots = new ArrayList<Spot>();
			for (Sample sample : samples) {
				spots.add(sample.getSpot());
			}

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
				List<Sample> samples = getFileSamples(filename);
				spots = new ArrayList<Spot>();
				for (Sample sample : samples) {
					spots.add(sample.getSpot());
				}
			}
		}

		JFrame frame = new JFrame("Raisa Visualizer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		visualizer.update(spots);
		frame.add(visualizer);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		visualizer.requestFocusInWindow();
	}

}
