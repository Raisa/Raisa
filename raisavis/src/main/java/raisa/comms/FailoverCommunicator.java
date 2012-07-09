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
	public void sendPackage(byte[] bytes) {
		if (currentCommunicator != null) {
			currentCommunicator.sendPackage(bytes);
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
}
