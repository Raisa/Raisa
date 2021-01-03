package raisa.comms.serial;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * The old serial communicator using rxtx-library: https://github.com/rxtx/rxtx
 */
public class RxtxSerialCommunicator extends AbstractSerialCommunicator implements SerialPortEventListener {
	private static final Logger log = LoggerFactory.getLogger(RxtxSerialCommunicator.class);
	/** The port we're normally going to use. */
	private SerialPort serialPort;
	private CommPortIdentifier portId = null;

	@Override
	public boolean connect() {
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			log.error("Could not find COM port.");
			return false;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = new InputStreamReader(serialPort.getInputStream(), US_ASCII);

			serialWriter = new SerialWriter(serialPort.getOutputStream());
			serialWriterThread = new Thread(serialWriter, "raisavis-SerialCommunicator");
			serialWriterThread.start();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (TooManyListenersException | PortInUseException | UnsupportedCommOperationException | IOException e) {
			System.err.println(e.toString());
			return false;
		}

		return true;
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	@Override
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
		if (serialWriter != null) {
			serialWriter.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (!active) {
			return;
		}
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			int tmp;
			try {
				while ((tmp = input.read()) != -1 && active) {
					handleSampleByte(tmp);
				}
			} catch (IOException e) {
				log.error("Error in processing serial event", e);
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		if(portId != null) {
			builder.append(":");
			builder.append(portId.getName());
		}
		return builder.toString();
	}

}


