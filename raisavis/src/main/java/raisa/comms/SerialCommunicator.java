package raisa.comms;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
	private InputStreamReader input;
	
	/** Thread for sending control messages to serial */
	private Thread serialWriterThread;
	private SerialWriter serialWriter;
	
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 111111;
	private static final String ACK_STR = "ACK";	
	
	private List<SensorListener> sensorListeners = new ArrayList<SensorListener>();
	private CommPortIdentifier portId = null;
	private boolean active = false;
	private String unfinishedSample = "";
	
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
			input = new InputStreamReader(serialPort.getInputStream());
			
			serialWriter = new SerialWriter(serialPort.getOutputStream());
			serialWriterThread = new Thread(serialWriter);
			serialWriterThread.start();

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
		log.debug("Serial event: {}", oEvent.getEventType());
		if (!active) {
			return;
		}
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {			
				SampleParser parser = new SampleParser();
				int tmp;
				while ((tmp = input.read()) != -1) {
					unfinishedSample += String.valueOf((char)tmp);
					if (!unfinishedSample.endsWith("\n")) {
						continue;
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
			} catch (Exception e) {
				log.error("Error in processing serial event", e);
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
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

	@Override
	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			log.info((active ? "Activating" : "Passivating") + " SerialCommunicator");
		}
	}

}

class SerialWriter implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SerialWriter.class);
	
	private static final long RETRANSMISSION_DELAY = 500L;
	
	private OutputStream output;
	private ControlMessage latestMessage;
	private int ackId = -1;
	private boolean running = true;
	
	public SerialWriter(OutputStream output) {
		this.output = output;
	}
	
	public void setAckReceived(int id) {
		this.ackId = id;
	}
	
	public void sendMessage(ControlMessage message) {
		this.latestMessage = message;
	}
	
	public void close() {
		this.running = false;
	}
	
	@Override
	public void run() {
		while (running)	{
			if (latestMessage != null && (latestMessage.getId() % 10) != ackId) {
				try {
					output.write(latestMessage.toSerialMessage());
					output.flush();
					log.debug(latestMessage.toString());
				} catch (IOException e) {
					log.error("Error sending command to serial", e);
				}
			}
			try {
				Thread.sleep(RETRANSMISSION_DELAY);
			} catch (InterruptedException e) {
				log.debug("Thread sleep interrupted", e);
			}
		}
	}
		
}

