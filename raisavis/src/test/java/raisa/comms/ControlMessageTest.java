package raisa.comms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder(NAME_ASCENDING)
public class ControlMessageTest {

	private final ControlMessage controlMessage = new ControlMessage(5, -4, true, 42, 10, false, true, CameraResolution.NOCHANGE, false);
	{
		controlMessage.setTimestamp(10l);
	}

	@Test
	public void jsonDeserialization() {
		String value = "{\"id\":0,\"leftSpeed\":5,\"rightSpeed\":-4,\"lights\":true,\"servos\":true,\"timestamp\":10,\"unsupported-field\": 'abc',\"panServoAngle\":42,\"tiltServoAngle\":10,\"cameraResolution\":\"NOCHANGE\",\"takePicture\":false}";
		assertThat(ControlMessage.fromJson(value), is(controlMessage));
	}

	@Test
	public void jsonSerialization() {
		String value = "{\"id\":1,\"leftSpeed\":5,\"rightSpeed\":-4,\"panServoAngle\":42,\"tiltServoAngle\":10,\"lights\":true,\"takePicture\":false,\"servos\":true,\"cameraResolution\":\"NOCHANGE\",\"rawValues\":false,\"timestamp\":10}";
		assertThat(controlMessage.toJson(), is(value));
	}

}
