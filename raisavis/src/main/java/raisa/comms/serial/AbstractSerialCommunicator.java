package raisa.comms.serial;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.Communicator;
import raisa.comms.ControlMessage;
import raisa.comms.SampleParser;
import raisa.comms.SensorListener;

public abstract class AbstractSerialCommunicator implements Communicator {

	private static final Logger log = LoggerFactory.getLogger(AbstractSerialCommunicator.class);
	/** Default bits per second for COM port. */
	protected static final String ACK_STR = "ACK";
	/** Milliseconds to block while waiting for port open */
	protected static final int TIME_OUT = 2000;
	protected static final int DATA_RATE = 111111;
	protected static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac OS/X
			"/dev/tty.usbmodemfa131", "/dev/tty.usbmodemfd111", "/dev/ttys0", "/dev/ttys000", "/dev/tty001", "/dev/ttyUSB0", "/dev/ttyS0", "ttyACM0", // Linux
			"COM3", // Windows
	};

	private String unfinishedSample = "";
	private final List<SensorListener> sensorListeners = new ArrayList<SensorListener>();
	private final SampleParser parser = new SampleParser();
	protected boolean active = false;
	/** Buffered input stream from the port */
	protected InputStreamReader input;
	/** Thread for sending control messages to serial */
	protected Thread serialWriterThread;
	protected SerialWriter serialWriter;

	@Override
	public Communicator addSensorListener(SensorListener ... sensorListeners) {
		for(SensorListener sensorListener: sensorListeners) {
			this.sensorListeners.add(sensorListener);
		}
		return this;
	}

	@Override
	public Communicator removeSensorListener(SensorListener ... sensorListeners) {
		for(SensorListener sensorListener: sensorListeners) {
			this.sensorListeners.remove(sensorListener);
		}
		return this;
	}

	@Override
	public void sendPackage(ControlMessage controlMessage) {
		if (!active) {
			return;
		}
		if (serialWriter != null) {
			serialWriter.sendMessage(controlMessage);
			synchronized(serialWriterThread) {
				serialWriterThread.notifyAll();
			}
		}
	}

	@Override
	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			log.info((active ? "Activating" : "Passivating") + " SerialCommunicator");
		}
	}

	protected void handleSampleByte(int tmp) {
		unfinishedSample += String.valueOf((char)tmp);
		if (!unfinishedSample.endsWith("\n")) {
			return;
		}
		if (unfinishedSample.startsWith(ACK_STR)) {
			log.debug("Sample {}", unfinishedSample);
			try {
				int ackId = Character.getNumericValue(unfinishedSample.charAt(3));
				serialWriter.setAckReceived(ackId);
				synchronized(serialWriterThread) {
					serialWriterThread.notifyAll();
				}
			} catch (Throwable nex) {
				log.warn("Invalid acknowledgement: " + unfinishedSample);
			}
		} else if (!parser.isValid(unfinishedSample)) {
			log.warn("Invalid sample! {}", unfinishedSample);
		} else {
			for (SensorListener sensorListener : sensorListeners) {
				sensorListener.sampleReceived(unfinishedSample);
			}
		}
		unfinishedSample = "";
	}

	@Override
	public abstract void close();

}
