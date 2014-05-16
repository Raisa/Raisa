package raisa.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;
import raisa.domain.WorldModel;

public class FileBasedSimulation implements Runnable, VisualizerConfigListener {

	private static final Logger log = LoggerFactory.getLogger(FileBasedSimulation.class);

	private boolean active = false;
	private final WorldModel world;
	private Thread simulationThread;

	private int nextSample = 0;
	private List<String> samples = new ArrayList<String>();
	private boolean delayed;
	private boolean stepSimulation;

	public FileBasedSimulation(WorldModel world) {
		this.world = world;
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}

	public void setSamples(List<String> samples, boolean delayed) {
		this.samples = samples;
		this.delayed = delayed;
		this.nextSample = 0;
	}

	public void setStepSimulation(boolean stepSimulation) {
		this.stepSimulation = stepSimulation;
	}

	public void start() {
		if (active || samples.size()==0) {
			return;
		}
		active = true;
		simulationThread = new Thread(this, "raisavis-FileBasedSimulation");
		simulationThread.start();
	}

	public void stop() {
		active = false;
	}

	public void reset() {
		active = false;
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			log.error("interrupted", e);
		}
		samples = new ArrayList<String>();
	}

	@Override
	public void run() {
		log.info("Starting simulation");
		while (nextSample < samples.size() && active) {
			if (delayed) {
				while (stepSimulation) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						log.error("interrupted", e);
					}
				}
				stepSimulation = true;
			}
			world.sampleReceived(samples.get(nextSample));
			++nextSample;
		}
		active = false;
		log.info("Stopping simulation");
	}

	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (config.isChanged(VisualizerConfigItemEnum.INPUT_OUTPUT_TARGET)) {
			switch (config.getInputOutputTarget()) {
			case FILE_SIMULATION:
				start();
				break;
			default:
				stop();
				break;
			}
		}
	}

}
