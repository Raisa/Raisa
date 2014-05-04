package raisa.ui.controls.sixaxis;

import static raisa.ui.controls.sixaxis.SixaxisInput.PayloadField.BUTTON_PAD;
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

	// Direction pad input code => DirectionPad enum value
	private static Map<Integer, DirectionPad> directionPadLookup = new HashMap<Integer, DirectionPad>();

	// Button pad input code => ButtonPad enum value
	private static Map<Integer, ButtonPad> buttonPadLookup = new HashMap<Integer, ButtonPad>();

	private static boolean nativeLibLoaded = false;

	private static SixaxisInput instance;
	private SixaxisDeviceReader deviceReader;
	private Thread deviceReaderThread;
	private final Map<DirectionPad, AbstractButton> movementButtons = new HashMap<DirectionPad, AbstractButton>();
	private final Map<ButtonPad, AbstractButton> actionButtons = new HashMap<ButtonPad, AbstractButton>();
	private final Map<String, AbstractButton> panAndTiltButtons = new HashMap<String, AbstractButton>();

	public static SixaxisInput getInstance() {
		if (instance == null) {
			synchronized(SixaxisInput.class) {
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
			}
		}
		return instance;
	}

	enum PayloadField {
		DIRECTION_PAD(2),
		BUTTON_PAD(3),
		JOYSTICK_RIGHT_X(8),
		JOYSTICK_RIGHT_Y(9);
		private final int fieldNo;

		private PayloadField(int fieldNo) {
			this.fieldNo = fieldNo;
		}

		public int getFieldNo() {
			return fieldNo;
		}
	}

	enum ButtonPad {
		LEFT2(0x01),
		RIGHT2(0x02),
		LEFT1(0x04),
		RIGHT1(0x08),
		TRIANGLE(0x10),
		CIRCLE(0x20),
		CROSS(0x40),
		SQUARE(0x80);
		private final int code;
		private ButtonPad(int code) {
			this.code = code;
			buttonPadLookup.put(Integer.valueOf(code), this);
		}
		public int getCode() {
			return code;
		}
	}

	enum DirectionPad {
		SELECT(0x01),
		BTN_L3(0x02),
		BTN_R3(0x04),
		START(0x08),
		UP(0x10),
		RIGHT(0x20),
		DOWN(0x40),
		LEFT(0x80);
		private final int code;
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

	public void registerMovementButtons(AbstractButton up,
			AbstractButton down, AbstractButton left, AbstractButton right, AbstractButton stop) {
		movementButtons.put(DirectionPad.UP, up);
		movementButtons.put(DirectionPad.DOWN, down);
		movementButtons.put(DirectionPad.LEFT, left);
		movementButtons.put(DirectionPad.RIGHT, right);
		actionButtons.put(ButtonPad.SQUARE, stop);
	}

	public void registerActionButtons(AbstractButton lights,
			AbstractButton takePic, AbstractButton servos) {
		actionButtons.put(ButtonPad.TRIANGLE, lights);
		actionButtons.put(ButtonPad.CIRCLE, takePic);
		actionButtons.put(ButtonPad.CROSS, servos);
	}

	public void registerPanAndTiltButtons(AbstractButton up,
			AbstractButton down, AbstractButton left, AbstractButton right, AbstractButton center) {
		panAndTiltButtons.put("UP", up);
		panAndTiltButtons.put("DOWN", down);
		panAndTiltButtons.put("LEFT", left);
		panAndTiltButtons.put("RIGHT", right);
		movementButtons.put(DirectionPad.BTN_R3, center);
	}

	protected void handleInput(int[] buf) {
		doClick(movementButtons.get(directionPadLookup.get(Integer.valueOf(buf[DIRECTION_PAD.getFieldNo()]))));
		doClick(actionButtons.get(buttonPadLookup.get(Integer.valueOf(buf[BUTTON_PAD.getFieldNo()]))));

		// crappy handling for pan & tilt servos below
		int joystickRightX = buf[PayloadField.JOYSTICK_RIGHT_X.getFieldNo()];
		if (joystickRightX < 100) {
			doClick(panAndTiltButtons.get("LEFT"));
		} else if (joystickRightX > 155) {
			doClick(panAndTiltButtons.get("RIGHT"));
		}
		int joystickRightY = buf[PayloadField.JOYSTICK_RIGHT_Y.getFieldNo()];
		if (joystickRightY < 100) {
			doClick(panAndTiltButtons.get("UP"));
		} else if (joystickRightY > 155) {
			doClick(panAndTiltButtons.get("DOWN"));
		}
	}

	private void doClick(AbstractButton button) {
		if (button != null) {
			button.doClick();
		}
	}

	class SixaxisDeviceReader implements Runnable {
		private static final int VENDOR_ID = 1356;
		private static final int PRODUCT_ID = 616;
		private static final long READ_UPDATE_DELAY_MS = 150L;
		private static final int INPUT_BUFFER_LENGTH = 64;

		private boolean running = false;
		private final HIDDevice dev;
		private final HIDManager hidMgr;
		private final SixaxisInput input;

		public SixaxisDeviceReader(SixaxisInput input) throws IOException {
			hidMgr = HIDManager.getInstance();
			dev = hidMgr.openById(VENDOR_ID, PRODUCT_ID, null);
			this.input = input;
		}

		public void stop() {
			running = false;
		}

		@Override
		public void run() {
			running = true;
			try {
				byte[] buf = new byte[INPUT_BUFFER_LENGTH];
				int[] intBuf = new int[INPUT_BUFFER_LENGTH];
				dev.enableBlocking();
				while (running) {
					dev.read(buf);
					//printPayload(buf, n);
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
				//System.gc();
			}
		}

	}

//	private void printPayload(byte[] buf, int n) {
//		for(int i=0; i<n; i++) {
//			int v = buf[i];
//			if (v<0) {
//				v = v+256;
//			}
//			String hs = Integer.toHexString(v);
//			if (v<16) {
//				System.err.print("0");
//			}
//			System.err.print(hs + " ");
//		}
//		System.err.println("");
//	}

}
