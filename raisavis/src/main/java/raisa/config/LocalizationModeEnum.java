package raisa.config;

public enum LocalizationModeEnum {

	NONE(0), PARTICLE_FILTER(1), SLAM(2);
	
	private int index;
	
	private LocalizationModeEnum(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
