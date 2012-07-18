package raisa.config;

public enum InputOutputTargetEnum {

	FILE_SIMULATION(0), RAISA_ACTUAL(1), REALTIME_SIMULATOR(2);

	 private int index;
	 
	 private InputOutputTargetEnum(int index) {
		 this.index = index;
	 }
	 
	 public int getIndex() {
		 return index;
	 }
}
