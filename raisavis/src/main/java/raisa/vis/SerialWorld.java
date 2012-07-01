package raisa.vis;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class SerialWorld implements SerialPortEventListener {
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/tty.usbmodemfd111",
			"/dev/ttys0",
			"/dev/ttys000",
			"/dev/tty001",			
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
			};
	/** Buffered input stream from the port */
	private InputStream input;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	/** Spots for drawing the picture */
	private List<Spot> spots;
	private VisualizerPanel visualizer;
	
	public SerialWorld(VisualizerPanel visualizer) {
		this.spots = new ArrayList<Spot>();
		this.visualizer = visualizer;
	}
	
	public void initialize() {
		CommPortIdentifier portId = null;
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
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
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
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		System.out.println("Serial event: " + oEvent.getEventType());
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				Scanner scanner = new Scanner(input).useDelimiter("\n");
				String line = scanner.next();
				while (line!=null) {
					System.out.println(new String(line));							
					if (!line.matches("J\\d+,\\d+[\n\r]+")) {
						System.out.println("Invalid sample!");
					} else {
						Sample s = new Sample(0,0,line);
						this.spots.add(s.getSpot());
						this.visualizer.update(spots);
					}
					line = scanner.next();
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
			this.visualizer.update(spots);
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

}

