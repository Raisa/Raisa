package raisa.comms;

public interface Communicator {

	void sendPackage(byte[] bytes);

	boolean connect();
}
