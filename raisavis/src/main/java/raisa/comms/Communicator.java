package raisa.comms;

public interface Communicator {

	void sendPackage(ControlMessage message);

	boolean connect();
	
	void addSensorListener(SensorListener sensorListener);
	void removeSensorListener(SensorListener sensorListener);
}
