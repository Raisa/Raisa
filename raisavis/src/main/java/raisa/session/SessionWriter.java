package raisa.session;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.Communicator;
import raisa.comms.ControlMessage;
import raisa.comms.SensorListener;

public class SessionWriter implements Communicator, SensorListener, Closeable, Flushable {
	private static final Logger logger = LoggerFactory.getLogger(SessionWriter.class);
	private final File controlDataFile;
	private final File sensorDataFile;
	private final File directory;
	private PrintWriter controlDataOutput;
	private PrintWriter sensorDataOutput;
	private long start;

	public SessionWriter(File directory, String prefix) {
		this.directory = directory;
		controlDataFile = new File(directory, prefix + ".control");
		sensorDataFile = new File(directory, prefix + ".sensor");
	}

	synchronized public void start() throws FileNotFoundException {
		logger.info("SessionWriter starting");
		if (!directory.exists()) {
			try {
				logger.debug("Creating directory {}", directory);
				FileUtils.forceMkdir(directory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		controlDataOutput = new PrintWriter(new BufferedOutputStream(new FileOutputStream(controlDataFile)));
		sensorDataOutput = new PrintWriter(new BufferedOutputStream(new FileOutputStream(sensorDataFile)));
		start = System.currentTimeMillis();
	}

	@Override
	synchronized public void sendPackage(ControlMessage message) {
		if (controlDataOutput == null) {
			return;
		}
		// TODO add getTimestamp() as a new field to data
		// TODO move controlMessage.timestamp to here
		controlDataOutput.println(message.toJson());
	}

	@Override
	synchronized public void sampleReceived(String sample) {
		if (sensorDataOutput == null) {
			return;
		}
		// TODO add getTimestamp() as a new field to data
		sensorDataOutput.println(sample);
	}

	@Override
	public boolean connect() {
		return true;
	}

	private long getTimestamp() {
		return System.currentTimeMillis() - start;
	}

	@Override
	synchronized public void close() throws IOException {
		logger.info("SessionWriter closed");
		IOUtils.closeQuietly(controlDataOutput);
		IOUtils.closeQuietly(sensorDataOutput);
		controlDataOutput = null;
		sensorDataOutput = null;
	}

	@Override
	public void flush() throws IOException {
		flush(controlDataOutput);
		flush(sensorDataOutput);
	}

	private void flush(Flushable flushable) throws IOException {
		if (flushable != null) {
			flushable.flush();
		}
	}

	@Override
	public void addSensorListener(SensorListener sensorListener) {
		// no-op		
	}

	@Override
	public void removeSensorListener(SensorListener sensorListener) {
		// no-op
	}
}
