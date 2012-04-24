package raisa.vis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;

public class SerialWorld extends Observable implements SerialPortEventListener {
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
			};
	/** Buffered input stream from the port */
	private BufferedReader input;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	/** Spots for drawing the picture */
	private List<Spot> spots;
	
	public SerialWorld() {
		this.spots = new ArrayList<Spot>();
	}
	
	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

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
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream(),"ASCII"));

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
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int i = 0;
				String line = input.readLine();
				while (line!=null) {
					System.out.print(new String(line));							
					if (!line.matches("J\\d+,\\d+")) {
						System.out.print("Invalid sample!");
					} else {
						Sample s = new Sample(0,0,line);
						this.spots.add(s.getSpot());
						this.notifyObservers(spots);
					}
					line = input.readLine();
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
			this.notifyObservers(spots);
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

}

