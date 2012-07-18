package raisa.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.domain.Sample;
import raisa.domain.SampleListener;
import raisa.domain.WorldModel;

public class SimulatorFilter implements SampleListener, VisualizerConfigListener {

	private static final Logger log = LoggerFactory.getLogger(SimulatorFilter.class);
			
	private RobotSimulator robotSimulator;
	private WorldModel world;
	private boolean active;
	
	public SimulatorFilter(RobotSimulator simulator, WorldModel world) {
		this.robotSimulator = simulator;
		this.world = world;
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}
	@Override
	public void sampleAdded(Sample sample) {

	}	
	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (config.isChanged(VisualizerConfigItemEnum.LOCALIZATION_MODE, VisualizerConfigItemEnum.INPUT_OUTPUT_TARGET)) {
			switch (config.getInputOutputTarget()) {
			case REALTIME_SIMULATOR:
				switch (config.getLocalizationMode()) {
				case NONE:
					if (!active) {
						world.addSampleListener(this);
						active = true;
						log.info("Activating simulator world state updates");
					}
					return;
				}
			}
			if (active) {
				log.info("Deactivating simulator world state updates");
				world.removeSampleListener(this);						
				active = false;
			}
		}
	}	
	
	
}