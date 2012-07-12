package raisa.session;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.Communicator;
import raisa.comms.ControlMessage;
import raisa.comms.SensorListener;

public class SessionWriter implements Communicator, SensorListener, Closeable, Flushable {
	private static final Logger logger = LoggerFactory.getLogger(SessionWriter.class);
	private File controlDataFile;
	private File sensorDataFile;
	private PrintWriter controlDataOutput;
	private PrintWriter sensorDataOutput;
	private long start;
	private String prefix;
	private File sessionDirectory;
	private File mainDirectory;

	public SessionWriter(File mainDirectory, String prefix) {
		this.mainDirectory = mainDirectory;
		this.prefix = prefix;
	}

	synchronized public void start() throws IOException {
		logger.info("SessionWriter starting");
		if (isCapturingData()) {
			logger.warn("Already capturing data");
			return;
		}

		sessionDirectory = getSubDirectory(mainDirectory);
		controlDataFile = new File(sessionDirectory, prefix + ".control");
		sensorDataFile = new File(sessionDirectory, prefix + ".sensor");

		if (!sessionDirectory.exists()) {
			try {
				logger.debug("Creating directory {}", sessionDirectory);
				FileUtils.forceMkdir(sessionDirectory);
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
		if (!isCapturingData()) {
			return;
		}
		// synchronize control messages using same clock as sensor readings
		message.setTimestamp(getTimestamp());
		controlDataOutput.println(message.toJson());
		controlDataOutput.flush();
	}

	@Override
	synchronized public void sampleReceived(String sample) {
		if (!isCapturingData()) {
			return;
		}
		// synchronize sensor readings using same clock as control messages 
		String timestampedSample = sample.replaceAll("TI\\d+", "TI" + getTimestamp());
		sensorDataOutput.println(timestampedSample);
		sensorDataOutput.flush();
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
		IOUtils.closeQuietly(controlDataOutput);
		IOUtils.closeQuietly(sensorDataOutput);
		controlDataOutput = null;
		sensorDataOutput = null;
		logger.info("SessionWriter closed");
	}

	synchronized public boolean isCapturingData() {
		return controlDataOutput != null;
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
	public Communicator addSensorListener(SensorListener... sensorListener) {
		// no-op
		return this;
	}

	@Override
	public Communicator removeSensorListener(SensorListener... sensorListener) {
		// no-op
		return this;
	}

	private File getSubDirectory(File mainDirectory) {
		return new File(mainDirectory, new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date()));
	}

}
