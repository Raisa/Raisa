package raisa.comms;

public class ConsoleCommunicator implements Communicator {

	@Override
	public void sendPackage(byte[] bytes) {
		System.out.println(new String(bytes));
	}

	@Override
	public boolean connect() {
		return true;
	}

}
