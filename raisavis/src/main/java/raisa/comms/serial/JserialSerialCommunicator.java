package raisa.comms.serial;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_RECEIVED;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * The new serial communicator using jSerialComm-library: http://fazecast.github.io/jSerialComm/
 */
public class JserialSerialCommunicator extends AbstractSerialCommunicator implements SerialPortDataListener {

	private static final Logger log = LoggerFactory.getLogger(JserialSerialCommunicator.class);
	/** The port we're normally going to use. */
	private SerialPort serialPort;

	@Override
	public boolean connect() {
		SerialPort[] commPorts = SerialPort.getCommPorts();

		// iterate through, looking for the port
		for (SerialPort commPort : commPorts) {
			for (String portName : PORT_NAMES) {
				if (commPort.getSystemPortName().equals(portName)) {
					serialPort = commPort;
					break;
				}
			}
		}

		if (serialPort == null) {
			log.error("Could not find COM port.");
			return false;
		}

		// open serial port, and use class name for the appName.
		if (!serialPort.openPort(TIME_OUT)) {
			log.error("Failed to open COM port.");
			return false;
		}
		// set port parameters
		serialPort.setComPortParameters(DATA_RATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

		// open the streams
		input = new InputStreamReader(serialPort.getInputStream(), US_ASCII);

		serialWriter = new SerialWriter(serialPort.getOutputStream());
		serialWriterThread = new Thread(serialWriter, "raisavis-SerialCommunicator");
		serialWriterThread.start();

		serialPort.addDataListener(this);
		return true;
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	@Override
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.closePort();
		}
		if (serialWriter != null) {
			serialWriter.close();
		}
	}

	@Override
	public int getListeningEvents() {
		return LISTENING_EVENT_DATA_RECEIVED;
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (!active) {
			return;
		}
		byte[] tmp = new byte[1];
		while (active) {
			serialPort.readBytes(tmp, 1);
			handleSampleByte(tmp[0]);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		if(serialPort != null) {
			builder.append(":");
			builder.append(serialPort.getDescriptivePortName());
		}
		return builder.toString();
	}

}
