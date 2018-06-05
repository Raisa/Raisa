package raisa.comms;

public enum CameraResolution {

	NOCHANGE(0), R160x120(1), R320x240(2), R640x480(3);

	private final int value;

	private CameraResolution(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
