package raisa.comms;

public class ConsoleCommunicator implements Communicator {

	@Override
	public void sendPackage(ControlMessage controlMessage) {
		System.out.println(controlMessage);
	}

	@Override
	public boolean connect() {
		return true;
	}

}
