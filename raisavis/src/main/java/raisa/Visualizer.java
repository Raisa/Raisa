package raisa;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

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
	
	private static final String OPTION_LOCALIZATION = "localization";
	private static final String OPTION_HELP = "help";
	private static final String OPTION_MAP = "map";
	private static final String OPTION_IOMODE = "iomode";
	private static final String OPTION_SAMPLEFILE = "samplefile";
	private static final String OPTION_CONTROLFILE = "controlfile";

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
	
	private static Options createCmdLineOptions() {
		Options options = new Options();
		options.addOption(OPTION_HELP, false, "print this help text");
		options.addOption(OPTION_MAP, true, "map file in PNG-format");
		options.addOption(OPTION_IOMODE, true, "'simfile' (default), 'serial' or 'simulator'");
		options.addOption(OPTION_LOCALIZATION, true, "'none' (default), 'slam' or 'particle_filter'");
		options.addOption(OPTION_SAMPLEFILE, true, "'example' or simulation samples file for simfile iomode");
		options.addOption(OPTION_CONTROLFILE, true, "control file for actual robot or simulator");
		return options;
	}

	public static void main(String[] args) throws Exception {
		Options options = createCmdLineOptions();		
	    CommandLineParser parser = new PosixParser();
	 
		// initialize configuration based on command line arguments
	    try {
		    CommandLine line = parser.parse(options, args);			
		    if (line.hasOption(OPTION_HELP)) {
			    HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "Visualizer", options );
				System.exit(0);
		    }
			VisualizerConfig config = VisualizerConfig.getInstance();
			
			if (line.hasOption(OPTION_LOCALIZATION)) {
				String val = line.getOptionValue(OPTION_LOCALIZATION);
				if ("none".equalsIgnoreCase(val)) {
					config.setLocalizationMode(LocalizationModeEnum.NONE);
				} else if ("slam".equalsIgnoreCase(val)) {
					config.setLocalizationMode(LocalizationModeEnum.SLAM);
				} else if ("particle_filter".equalsIgnoreCase(val)) {
					config.setLocalizationMode(LocalizationModeEnum.PARTICLE_FILTER);
				} else {
					throw new ParseException("Invalid localization \"" + val + "\"");
				}
			} else {
				config.setLocalizationMode(LocalizationModeEnum.NONE);				
			}
			
			if (line.hasOption(OPTION_IOMODE)) {
				String val = line.getOptionValue(OPTION_IOMODE);
				if ("simfile".equals(val)) {
					config.setInputOutputTarget(InputOutputTargetEnum.FILE_SIMULATION);
				} else if ("simulator".equals(val)) {
					config.setInputOutputTarget(InputOutputTargetEnum.REALTIME_SIMULATOR);			
				} else if ("serial".equals(val)) {
					config.setInputOutputTarget(InputOutputTargetEnum.RAISA_ACTUAL);			
				} else {
					throw new ParseException("Invalid iomode \"" + val + "\"");
				}
			} else {
				config.setInputOutputTarget(InputOutputTargetEnum.FILE_SIMULATION);				
			}
			
			final WorldModel worldModel = new WorldModel();
			final VisualizerFrame frame = new VisualizerFrame(worldModel);	
			
			if (line.hasOption(OPTION_SAMPLEFILE)) {
				String val = line.getOptionValue(OPTION_SAMPLEFILE);
				if ("example".equals(val)) {
					frame.spawnSampleSimulationThread(getExampleSamples(), true);
				} else {
					frame.loadSamples(val);
				}
			} 

			if (line.hasOption(OPTION_CONTROLFILE)) {
				String val = line.getOptionValue(OPTION_CONTROLFILE);
				frame.loadReplay(val);
			} 
			
			if (line.hasOption(OPTION_MAP)) {
				String val = line.getOptionValue(OPTION_MAP);
				frame.loadMap(val);
			} 

			config.setChanged(VisualizerConfigItemEnum.ALL_CONFIG_ITEMS);
			config.notifyVisualizerConfigListeners();			
			
			frame.open();
	    } catch (ParseException pex) {
	        System.err.println( "Parsing failed.  Reason: " + pex.getMessage() );
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "Visualizer", options );
			System.exit(0);
	    }
	}
}
