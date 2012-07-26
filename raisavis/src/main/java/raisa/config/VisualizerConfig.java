package raisa.config;

import java.util.ArrayList;
import java.util.List;

public class VisualizerConfig {
		
	private List<VisualizerConfigListener> visualizerConfigListeners;
	private static VisualizerConfig instance;
	
	private InputOutputTargetEnum inputOutputTarget = InputOutputTargetEnum.FILE_SIMULATION;
	private LocalizationModeEnum localizationMode = LocalizationModeEnum.NONE;	
	private List<VisualizerConfigItemEnum> changedConfigs;

	private boolean displayMap = true;
	private boolean displayTrail = true;
	private boolean displayRobot = true;
	private boolean displaySimulator = true;
	private boolean displayParticles = true;
	private boolean displaySonarScan = true;
	private boolean displayIrScan = true;
	private int displayMinAgeForParticles = 0;
	
	private VisualizerConfig() {
		this.visualizerConfigListeners = new ArrayList<VisualizerConfigListener>();
		this.changedConfigs = new ArrayList<VisualizerConfigItemEnum>();
	}
	
	public static VisualizerConfig getInstance() {
		if (instance == null) {
			instance = new VisualizerConfig();
		}
		return instance;
	}
	
	public InputOutputTargetEnum getInputOutputTarget() {
		return this.inputOutputTarget;
	}

	public void setInputOutputTarget(InputOutputTargetEnum inputOutputTarget) {
		if (this.inputOutputTarget != inputOutputTarget) {
			this.inputOutputTarget = inputOutputTarget;
			this.changedConfigs.add(VisualizerConfigItemEnum.INPUT_OUTPUT_TARGET);
		}
	}	
	
	public void setLocalizationMode(LocalizationModeEnum localizationMode) {
		if (this.localizationMode != localizationMode) {
			this.localizationMode = localizationMode;
			this.changedConfigs.add(VisualizerConfigItemEnum.LOCALIZATION_MODE);
		}
	}

	public LocalizationModeEnum getLocalizationMode() {
		return localizationMode;
	}	
	
	public boolean isChanged(VisualizerConfigItemEnum ... configItems) {
		for (VisualizerConfigItemEnum configItem : configItems) {
			if (changedConfigs.contains(configItem)) {
				return true;
			}
		}
		return changedConfigs.contains(VisualizerConfigItemEnum.ALL_CONFIG_ITEMS);
	}
	
	public void addVisualizerConfigListener(VisualizerConfigListener listener) {
		visualizerConfigListeners.add(listener);
	}
	
	public void setChanged(VisualizerConfigItemEnum configItem) {
		changedConfigs.add(configItem);
	}
		
	public void notifyVisualizerConfigListeners() {
		for (VisualizerConfigListener listener : visualizerConfigListeners) {
			listener.visualizerConfigChanged(this);
		}
		this.changedConfigs.clear();
	}

	public boolean isDisplayMap() {
		return displayMap;
	}

	public void setDisplayMap(boolean displayMap) {
		if(this.displayMap != displayMap) {
			this.displayMap = displayMap;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_MAP);
		}
	}

	public boolean isDisplayTrail() {
		return displayTrail;
	}

	public void setDisplayTrail(boolean displayTrail) {
		if(this.displayTrail != displayTrail) {
			this.displayTrail = displayTrail;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_TRAIL);
		}
	}

	public boolean isDisplayRobot() {
		return displayRobot;
	}

	public void setDisplayRobot(boolean displayRobot) {
		if(this.displayRobot != displayRobot) {
			this.displayRobot = displayRobot;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_ROBOT);
		}
	}

	public boolean isDisplaySimulator() {
		return displaySimulator;
	}

	public void setDisplaySimulator(boolean displaySimulator) {
		if(this.displaySimulator != displaySimulator) {
			this.displaySimulator = displaySimulator;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_SIMULATOR);
		}
	}

	public boolean isDisplayParticles() {
		return displayParticles;
	}

	public void setDisplayParticles(boolean displayParticles) {
		if(this.displayParticles != displayParticles) {
			this.displayParticles = displayParticles;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_PARTICLES);
		}
	}

	public boolean isDisplaySonarScan() {
		return displaySonarScan;
	}

	public void setDisplaySonarScan(boolean displaySonarScan) {
		if(this.displaySonarScan != displaySonarScan) {
			this.displaySonarScan = displaySonarScan;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_SONAR_SCAN);
		}
	}

	public boolean isDisplayIrScan() {
		return displayIrScan;
	}

	public void setDisplayIrScan(boolean displayIrScan) {
		if(this.displayIrScan != displayIrScan) {
			this.displayIrScan = displayIrScan;
			this.changedConfigs.add(VisualizerConfigItemEnum.DISPLAY_IR_SCAN);
		}
	}

	public int getDisplayMinAgeForParticles() {
		return displayMinAgeForParticles;
	}

	public void setDisplayMinAgeForParticles(int displayMinAgeForParticles) {
		this.displayMinAgeForParticles = displayMinAgeForParticles;
	}

}
