package raisa.comms;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailoverCommunicator implements Communicator {
	private static final Logger log = LoggerFactory.getLogger(FailoverCommunicator.class);
	private List<Communicator> communicators;
	private Communicator currentCommunicator = null;

	public FailoverCommunicator(Communicator... communicators) {
		this.communicators = Arrays.asList(communicators);
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

}
