package raisa.comms.controller;

import java.util.ArrayList;
import java.util.List;

import raisa.comms.ControllerListener;

public abstract class Controller {
	protected final List<ControllerListener> controlListeners = new ArrayList<ControllerListener>();

	protected void notifyControlListeners() {
		for (ControllerListener listener : controlListeners) {
			listener.controlsChanged(this);
		}
	}

	public void addContolListener(ControllerListener controlListener) {
		controlListeners.add(controlListener);
	}

	public abstract boolean getServos();	
	
	public abstract boolean getLights();

	public abstract int getLeftSpeed();

	public abstract int getRightSpeed();
	
	public abstract int getPanServoAngle();
	
	public abstract int getTiltServoAngle();

}
