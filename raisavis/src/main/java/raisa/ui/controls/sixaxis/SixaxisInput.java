package raisa.ui.controls.sixaxis;

import static raisa.ui.controls.sixaxis.SixaxisInput.PayloadField.DIRECTION_PAD;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;

public class SixaxisInput {
	private static final Logger log = LoggerFactory.getLogger(SixaxisInput.class);

	private static Map<Integer, DirectionPad> directionPadLookup = new HashMap<Integer, DirectionPad>();
	private static boolean nativeLibLoaded = false;

	private static SixaxisInput instance;
	private SixaxisDeviceReader deviceReader;
	private Thread deviceReaderThread;
	private Map<DirectionPad, AbstractButton> directionButtons = new HashMap<DirectionPad, AbstractButton>();

	public static SixaxisInput getInstance() {
		if (instance == null) {
			try {
		        System.loadLibrary("hidapi-jni");
		        nativeLibLoaded = true;
		    } catch(UnsatisfiedLinkError uex) {
		    	log.info("Failed to find hidapi-jni library for sixaxis");
		    } catch(Exception ex) {
		    	log.info("Failed to load library hidapi-jni", ex);
		    }
			instance = new SixaxisInput();
		}
		return instance;
	}

	enum PayloadField {
		DIRECTION_PAD(2);
		private final int fieldNo;

		private PayloadField(int fieldNo) {
			this.fieldNo = fieldNo;
		}

		public int getFieldNo() {
			return fieldNo;
		}
	}
	
	enum DirectionPad {
		UP(0x10),
		RIGHT(0x20),
		DOWN(0x40),
		LEFT(0x80);
		private int code;
		private DirectionPad(int code) {
			this.code = code;
			directionPadLookup.put(Integer.valueOf(code), this);
		}
		public int getCode() {
			return code;
		}
	}

	public void activate() {
		if (!nativeLibLoaded) {
			return;
		}
		try {
			deviceReader = new SixaxisDeviceReader(this);
			deviceReaderThread = new Thread(deviceReader);
			deviceReaderThread.start();
			log.info("Sixaxis controller activated");
		} catch(IOException iox) {
			log.info("Sixaxis controller not detected");
		}
	}

	public void passivate() {
		deviceReader.stop();
	}

	public void registerDirectionButtons(AbstractButton up,
			AbstractButton down, AbstractButton left, AbstractButton right) {
		directionButtons.put(DirectionPad.UP, up);
		directionButtons.put(DirectionPad.DOWN, down);
		directionButtons.put(DirectionPad.LEFT, left);
		directionButtons.put(DirectionPad.RIGHT, right);
	}

	protected void handleInput(int[] buf) {		
		AbstractButton directionButton = directionButtons.get(directionPadLookup.get(Integer.valueOf(buf[DIRECTION_PAD.getFieldNo()])));
		if (directionButton != null) {
			directionButton.doClick();
		}
	}

	class SixaxisDeviceReader implements Runnable {
		private static final int VENDOR_ID = 1356;
		private static final int PRODUCT_ID = 616;
		private static final long READ_UPDATE_DELAY_MS = 100L;
		private static final int INPUT_BUFFER_LENGTH = 64;

		private boolean running = false;
		private HIDDevice dev;
		private HIDManager hidMgr;
		private SixaxisInput input;

		public SixaxisDeviceReader(SixaxisInput input) throws IOException {
			hidMgr = HIDManager.getInstance();
			dev = hidMgr.openById(VENDOR_ID, PRODUCT_ID, null);
			this.input = input;
		}

		public void stop() {
			running = false;
		}

		public void run() {
			running = true;
			try {
				byte[] buf = new byte[INPUT_BUFFER_LENGTH];
				int[] intBuf = new int[INPUT_BUFFER_LENGTH];
				dev.enableBlocking();
				while (running) {
					dev.read(buf);
					for (int i=0; i<INPUT_BUFFER_LENGTH; i++) {
						intBuf[i] = (buf[i]<0) ? buf[i] + 256 : buf[i];
					}
					input.handleInput(intBuf);
					try {
						Thread.sleep(READ_UPDATE_DELAY_MS);
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			} catch (IOException e) {
				log.error("Error in reading sixaxis controller", e);
			} finally {
				try {
					dev.close();
				} catch (IOException e) {
					log.error("Error in closing sixaxis controller", e);
				}
				hidMgr.release();
				System.gc();
			}
		}

	}

}
