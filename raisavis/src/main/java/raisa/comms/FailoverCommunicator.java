package raisa.comms;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigItemEnum;
import raisa.config.VisualizerConfigListener;

public class FailoverCommunicator implements Communicator, VisualizerConfigListener {
	private static final Logger log = LoggerFactory.getLogger(FailoverCommunicator.class);
	private List<Communicator> communicators;
	private Communicator currentCommunicator = null;

	public FailoverCommunicator(Communicator... communicators) {
		this.communicators = Arrays.asList(communicators);
		VisualizerConfig.getInstance().addVisualizerConfigListener(this);
	}

	@Override
	public void sendPackage(ControlMessage controlMessage) {
		if (currentCommunicator != null) {
			currentCommunicator.sendPackage(controlMessage);
		}
	}

	@Override
	public boolean connect() {
		for (Communicator communicator : communicators) {
			try {
				if (communicator.connect()) {
					log.info("Connected communicator {}", communicator);
					currentCommunicator = communicator;
					return true;
				}
			} catch (Throwable t) {
				log.warn("Failed to connect {}", communicator);
			}
		}

		return false;
	}

	@Override
	public void setActive(boolean active) {
		for (Communicator communicator : communicators) {
			communicator.setActive(active);
		}
	}		
		
	@Override
	public Communicator addSensorListener(SensorListener ... sensorListeners) {
		for (Communicator communicator : communicators) {
			for(SensorListener sensorListener : sensorListeners) {
				communicator.addSensorListener(sensorListener);
			}
		}
		return this;
	}

	@Override
	public Communicator removeSensorListener(SensorListener ... sensorListeners) {
		for (Communicator communicator : communicators) {
			for(SensorListener listener : sensorListeners) {
				communicator.removeSensorListener(listener);
			}
		}
		return this;
	}
	
	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		if (config.isChanged(VisualizerConfigItemEnum.INPUT_OUTPUT_TARGET)) {
			switch (config.getInputOutputTarget()) {
			case RAISA_ACTUAL:
				setActive(true);
				break;
			default:
				setActive(false);
				break;
			}
		}
	}


}
