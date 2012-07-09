package raisa.comms;

public interface Communicator {

	void sendPackage(ControlMessage message);

	boolean connect();
}
