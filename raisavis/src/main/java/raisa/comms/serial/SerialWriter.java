package raisa.comms.serial;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.comms.ControlMessage;

/**
 * Sends the latest control message to Raisa until acknowledgement received.
 */
class SerialWriter implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SerialWriter.class);

	private static final long RETRANSMISSION_DELAY = 500L;

	private final OutputStream output;
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

	private boolean retryRequired(ControlMessage msg) {
		// retry if no acknowledgement received and the command is not "heavy"
		return msg.getId() % 10 != ackId && !msg.isTakePicture();
	}

	private void writeMessage(ControlMessage msg) {
		try {
			output.write(msg.toSerialMessage());
			output.flush();
		} catch (IOException e) {
			log.error("Error sending command to serial", e);
		}
	}

	private void retransmissionDelay() {
		try {
			Thread.sleep(RETRANSMISSION_DELAY);
		} catch (InterruptedException e) {
			log.debug("Thread sleep interrupted", e);
		}
	}

	@Override
	public void run() {
		while (running)	{
			if (latestMessage != null) {
				ControlMessage msg = latestMessage;
				int retryCounter = msg.getAndIncRetryCounter();
				if (retryCounter == 0 || retryRequired(msg)) {
					writeMessage(msg);
				}
			}
			retransmissionDelay();
		}
	}

}
