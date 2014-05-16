package raisa.session;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.Communicator;
import raisa.comms.ControlMessage;
import raisa.comms.SampleParser;
import raisa.comms.SensorListener;
import raisa.domain.samples.Sample;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class SessionWriter implements Communicator, SensorListener, Closeable, Flushable {
	private static final Logger logger = LoggerFactory.getLogger(SessionWriter.class);
	private File controlDataFile;
	private File sensorDataFile;
	private PrintWriter controlDataOutput;
	private PrintWriter sensorDataOutput;
	private long start;
	private final String prefix;
	private File sessionDirectory;
	private final File mainDirectory;
	private ExecutorService sessionWriterExecutor;

	@SuppressWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification="Static inner class would be overkill for small class")
	public SessionWriter(File mainDirectory, String prefix) {
		this.mainDirectory = mainDirectory;
		this.prefix = prefix;
		sessionWriterExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "raisavis-SessionWriter");
			}
		});
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

		controlDataOutput = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(controlDataFile)), US_ASCII));
		sensorDataOutput = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(sensorDataFile)), US_ASCII));
		start = System.currentTimeMillis();
	}

	@Override
	@SuppressWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification="Future value is uninteresting")
	public void sendPackage(final ControlMessage message) {
		sessionWriterExecutor.submit(new Runnable() {
			@Override
			public void run() {
				writeControlMessage(message);
			}
		});
	}

	synchronized private void writeControlMessage(ControlMessage message) {
		if (!isCapturingData()) {
			return;
		}
		// synchronize control messages using same clock as sensor readings
		message.setTimestamp(getTimestamp());
		controlDataOutput.println(message.toJson());
		controlDataOutput.flush();
	}

	@Override
	@SuppressWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification="Future value is uninteresting")
	public void sampleReceived(final String sample) {
		sessionWriterExecutor.submit(new Runnable() {
			@Override
			public void run() {
				writeSample(sample);
			}
		});
	}

	synchronized private void writeSample(String sample) {
		if (!isCapturingData()) {
			return;
		}
		// synchronize sensor readings using same clock as control messages
		String timestampedSample = sample.replaceAll("TI\\d+", "TI" + getTimestamp());
		sensorDataOutput.println(timestampedSample);
		sensorDataOutput.flush();
		writeImage(sample);
	}

	private void writeImage(String sample) {
		SampleParser sampleParser = new SampleParser();
		// first make a fast check
		if(!sampleParser.mayContainImage(sample)) {
			return;
		}
		// TODO sample is also parsed in WorldModel.java
		Sample parsedSample = sampleParser.parse(sample);
		if(parsedSample.getImage() == null && parsedSample.getImageBytes() == null) {
			return;
		}
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
		File imageFile = new File(sessionDirectory, prefix + "-camera-" + timestamp + ".jpeg");
		try {
			FileUtils.writeByteArrayToFile(imageFile, parsedSample.getImageBytes());
		} catch (IOException e) {
			logger.error("Failed to store image capture file " + imageFile.getPath(), e);
		}
	}

	@Override
	public boolean connect() {
		return true;
	}

	private long getTimestamp() {
		return System.currentTimeMillis() - start;
	}

	@Override
	synchronized public void close() {
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
	synchronized public void flush() throws IOException {
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

	@Override
	public void setActive(boolean active) {
		;
	}

}
