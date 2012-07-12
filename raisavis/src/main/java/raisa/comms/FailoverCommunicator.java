package raisa.comms;

import java.util.Arrays;
import java.util.List;

public class FailoverCommunicator implements Communicator {
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
					System.out.println("Connected communicator " + communicator);
					currentCommunicator = communicator;
					return true;
				}
			} catch (Throwable t) {
				System.out.println("Failed to connect " + communicator);
			}
		}

		return false;
	}

	@Override
	public void addSensorListener(SensorListener sensorListener) {
		for (Communicator communicator : communicators) {
			communicator.addSensorListener(sensorListener);
		}
	}

	@Override
	public void removeSensorListener(SensorListener sensorListener) {
		for (Communicator communicator : communicators) {
			communicator.removeSensorListener(sensorListener);
		}
	}

}
