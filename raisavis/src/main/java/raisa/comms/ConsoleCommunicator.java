package raisa.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleCommunicator implements Communicator {
	private static final Logger log = LoggerFactory.getLogger(ConsoleCommunicator.class);
	@Override
	public void sendPackage(ControlMessage controlMessage) {
		log.info("{}", controlMessage.toJson());
	}

	@Override
	public boolean connect() {
		return true;
	}
	
	@Override
	public void addSensorListener(SensorListener sensorListener) {
		// no-op
	}
	
	@Override
	public void removeSensorListener(SensorListener sensorListener) {
		// no-op
	}

}
