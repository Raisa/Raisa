package raisa.comms;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialCommunicator implements SerialPortEventListener, Communicator {
	private static final Logger log = LoggerFactory.getLogger(SerialCommunicator.class);
	/** The port we're normally going to use. */
	private SerialPort serialPort;
	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/tty.usbmodemfa131", "/dev/tty.usbmodemfd111", "/dev/ttys0", "/dev/ttys000", "/dev/tty001", "/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	/** Buffered input stream from the port */
	private InputStream input;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 111111;
	private List<SensorListener> sensorListeners = new ArrayList<SensorListener>();
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
			input = serialPort.getInputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
			return false;
		}

		return true;
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		log.debug("Serial event: {}", oEvent.getEventType());
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				Scanner scanner = new Scanner(input).useDelimiter("\n");
				String line = scanner.next();
				SampleParser parser = new SampleParser();
				while (line != null) {
					if (!parser.isValid(line)) {
						log.warn("Invalid sample! {}", line);
					} else {
						log.info("Sample {}", line);
						for (SensorListener sensorListener : sensorListeners) {
							sensorListener.sampleReceived(line);
						}
					}
					line = scanner.next();
				}
			} catch (Exception e) {
				log.error("Error in processing serial event", e);
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

	@Override
	public void sendPackage(ControlMessage controlMessage) {
		try {
			serialPort.getOutputStream().write(controlMessage.toSerialMessage());
			serialPort.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		if(portId != null) {
			builder.append(":");
			builder.append(portId.getName());
		}
		return builder.toString();
	}
}
