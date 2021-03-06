package raisa.comms;

public interface Communicator {

	void sendPackage(ControlMessage message);

	boolean connect();
	void close();
	void setActive(boolean active);
	
	Communicator addSensorListener(SensorListener ... sensorListener);
	Communicator removeSensorListener(SensorListener ... sensorListener);
}
