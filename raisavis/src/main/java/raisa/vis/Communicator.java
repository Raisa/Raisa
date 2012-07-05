package raisa.vis;

public interface Communicator {

	void sendPackage(byte[] bytes);

	boolean connect();
}
