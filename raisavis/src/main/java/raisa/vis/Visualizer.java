package raisa.vis;

import java.util.ArrayList;
import java.util.List;

/**
 * See http://arduino.cc/playground/Interfacing/Java for RXTX library setup
 */
public class Visualizer {
	private static List<Sample> getExampleSamples() {
		ExampleWorld1 world = new ExampleWorld1();
		List<Sample> samples = new ArrayList<Sample>();
		scenario2(world, samples);
		return samples;
	}

	private static void scenario1(ExampleWorld1 world, List<Sample> samples) {
		float x = 0.0f;
		float y = 0.0f;
		float heading = 0.0f;
		for (int o = 0; o < 50; ++o) {
			x = -100 + (o / 2) * 10;
			for (int i = 0; i < 50; ++i) {
				float angle = (3.0f * (float) Math.PI / 2.0f + i / 50.0f * (float) Math.PI) % (2.0f * (float) Math.PI);
				if (o % 2 == 1) {
					angle = 4.0f * (float) Math.PI - angle;
				}
				samples.add(new Sample(x, y, heading, world.sample(x, y, heading, angle)));
			}
		}
	}

	private static void scenario2(ExampleWorld1 world, List<Sample> samples) {
		float x = 0.0f;
		float y = 0.0f;
		float heading = (float)Math.toRadians(90.0f);
		// float heading = 3.0f * (float)Math.PI / 2.0f;
		for (int o = 0; o < 80; ++o) {
			for (int i = 0; i < 25; ++i) {
				x = -200 + (o * 25 + i) * 0.2f;
				float angleDegrees = -90.0f + (o % 7) + (i / 24.0f) * 180.0f;
				if (o % 2 == 1) {
					angleDegrees = 0.0f - angleDegrees;
				}
				float angle = (float)Math.toRadians(angleDegrees);
				samples.add(new Sample(x, y, heading, world.sample(x, y, heading, angle)));
			}
		}
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
				frame.spawnSampleSimulationThread(getExampleSamples(), true);
			} else if ("file".equals(inputMode)) {
				if (args.length != 2) {
					System.out.println("Missing filename");
				}
				String filename = args[1];
				frame.loadSimulation(filename);
			}
		}
	}
}
