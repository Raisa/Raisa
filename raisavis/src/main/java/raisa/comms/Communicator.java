package raisa.comms;

public interface Communicator {

	void sendPackage(ControlMessage message);

	boolean connect();
	
	Communicator addSensorListener(SensorListener ... sensorListener);
	Communicator removeSensorListener(SensorListener ... sensorListener);
}
