package raisa.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;

public class NoFilter implements SampleListener, VisualizerConfigListener {

	private static final Logger log = LoggerFactory.getLogger(NoFilter.class);
	
	private SimpleRobotMovementEstimator robotMovementEstimator;
	private WorldModel world;
	private boolean active;
	
	public NoFilter(WorldModel world) {
		this.robotMovementEstimator = new SimpleRobotMovementEstimator(false);
		this.world = world;
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}
	
	@Override
	public void sampleAdded(Sample sample) {
		Robot estimatedState = robotMovementEstimator.moveRobot(world.getLatestState(), sample);
		world.addState(estimatedState);
	}	
	
	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (config.isChanged(VisualizerConfigItemEnum.LOCALIZATION_MODE)) {
			switch (config.getLocalizationMode()) {
			case NONE:
				if (!active) {
					log.info("Activating unfiltered world state updates");
					world.addSampleListener(this);
					active = true;
				}
				return;
			}
			if (active) {
				log.info("Deactivating unfiltered world state updates");
				world.removeSampleListener(this);
				active = false;				
			}
		}
	}	
	
}
