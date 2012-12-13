package raisa;

import java.util.ArrayList;
import java.util.List;

import raisa.comms.SampleParser;
import raisa.config.InputOutputTargetEnum;
import raisa.config.LocalizationModeEnum;
import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.domain.WorldModel;
import raisa.domain.samples.Sample;
import raisa.test.ExampleWorld1;
import raisa.ui.VisualizerFrame;

/**
 * See http://arduino.cc/playground/Interfacing/Java for RXTX library setup
 */
public class Visualizer {
	private static List<Sample> getExampleSamples() {
		ExampleWorld1 world = new ExampleWorld1();
		List<Sample> samples = new ArrayList<Sample>();
		scenario1(world, samples);
		return samples;
	}

	private static void scenario1(ExampleWorld1 world, List<Sample> samples) {
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
				//Robot r = new Robot(heading, new Vector2D(x, y));
				Sample s = new SampleParser().parse(world.sample(x, y, heading, angle));
				samples.add(s);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
		// initialize configuration based on command line arguments
		final WorldModel worldModel = new WorldModel();
		final VisualizerFrame frame = new VisualizerFrame(worldModel);
		String inputMode = null;
		VisualizerConfig config = VisualizerConfig.getInstance();
		config.setLocalizationMode(LocalizationModeEnum.NONE);
		config.setInputOutputTarget(InputOutputTargetEnum.FILE_SIMULATION);
		if (args.length != 0) {
			inputMode = args[0];
			if ("test".equals(inputMode)) {
				config.setLocalizationMode(LocalizationModeEnum.PARTICLE_FILTER);
			} 
		}
		config.setChanged(VisualizerConfigItemEnum.ALL_CONFIG_ITEMS);
		config.notifyVisualizerConfigListeners();			
		
		// load visualizer data based on commmand line arguments
		if ("example".equals(inputMode)) {
			frame.spawnSampleSimulationThread(getExampleSamples(), true);
		} else if ("test".equals(inputMode)) {
			frame.loadMap("data/sightseeing2.png");
			frame.getParticleFilter().randomizeParticles(frame.getParticleFilter().getParticles().size());
			frame.loadData("data/eteinen-tyohuone-makkari2.data");
		} else if ("file".equals(inputMode)) {
			if (args.length != 2) {
				System.out.println("Missing simulation filename");
			}
			String filename = args[1];
			frame.loadSimulation(filename);
		} else if ("map".equals(inputMode)) {
			if (args.length != 2) {
				System.out.println("Missing map filename");
			}
			String filename = args[1];
			frame.loadMap(filename);
		}
		frame.open();
	}
}
