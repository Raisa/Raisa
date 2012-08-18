package raisa.comms;

import java.util.List;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplayController extends Controller {
	private static final Logger log = LoggerFactory.getLogger(ReplayController.class);
	private Communicator communicator;
	private final List<ControlMessage> controlMessages;
	private ControlMessage currentControlMessage;

	public ReplayController(Communicator communicator,
			final List<ControlMessage> controlMessages) {
		this.communicator = communicator;
		this.controlMessages = controlMessages;
		if (!controlMessages.isEmpty()) {
			currentControlMessage = controlMessages.get(0);
		}
	}
	
	public void start() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				long start = System.currentTimeMillis();
				log.info("Replay starting");
				for (ControlMessage controlMessage : controlMessages) {
					try {
						log.debug("Handle control message: {}", controlMessage);
						long diff = System.currentTimeMillis() - start;
						long messageTime = controlMessage.getTimestamp();
						if (diff > messageTime) {
							log.trace("Message time has passed. Sending immediately.");
							sendMessage(controlMessage);
						} else {
							long sleepTime = messageTime - diff;
							log.trace("Message time is in the future. Sending in {} msec", sleepTime);
							Thread.sleep(messageTime - diff);
							sendMessage(controlMessage);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				log.info("Replay finished");
				return null;
			}

		};
		worker.execute();
	}

	private synchronized void setCurrentControlMessage(
			ControlMessage controlMessage) {
		currentControlMessage = controlMessage;
	}

	private synchronized ControlMessage getCurrentControlMessage() {
		return currentControlMessage;
	}

	protected void sendMessage(ControlMessage controlMessage) {
		setCurrentControlMessage(controlMessage);
		notifyControlListeners();
		communicator.sendPackage(controlMessage);
	}

	@Override
	public boolean getLights() {
		return getCurrentControlMessage().isLights();
	}

	@Override
	public int getLeftSpeed() {
		return getCurrentControlMessage().getLeftSpeed();
	}

	@Override
	public int getRightSpeed() {
		return getCurrentControlMessage().getRightSpeed();
	}

	@Override
	public int getPanServoAngle() {
		return getCurrentControlMessage().getPanServoAngle();
	}	
	
	@Override
	public int getTiltServoAngle() {
		return getCurrentControlMessage().getTiltServoAngle();
	}	
	
}
