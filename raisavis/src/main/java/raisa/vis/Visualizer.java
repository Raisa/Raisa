package raisa.vis;

import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) throws Exception {
		final VisualizerFrame frame = new VisualizerFrame();
		
		final List<String> samples = new ArrayList<String>();
		if (args.length == 0) {
		} else {
			String inputMode = args[0];
			if ("serial".equals(inputMode)) {
				SerialWorld serialWorld = new SerialWorld(frame.getVisualizer());
				serialWorld.initialize();
			} else if ("example".equals(inputMode)) {
				samples.addAll(getExampleSamples());				
			} else if ("file".equals(inputMode)) {
				if (args.length != 2) {
					System.out.println("Missing filename");
				}
				String filename = args[1];
				frame.loadData(filename);
			}
		}
	}
}
