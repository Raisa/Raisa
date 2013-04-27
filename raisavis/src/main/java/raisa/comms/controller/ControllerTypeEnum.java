package raisa.comms.controller;

public enum ControllerTypeEnum {

	BASIC_CONTROLLER(0), PID_CONTROLLER(1);

	 private int index;
	 
	 private ControllerTypeEnum(int index) {
		 this.index = index;
	 }
	 
	 public int getIndex() {
		 return index;
	 }
}
