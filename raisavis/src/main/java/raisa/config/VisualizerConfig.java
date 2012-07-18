package raisa.config;

import java.util.ArrayList;
import java.util.List;

public class VisualizerConfig {
		
	private List<VisualizerConfigListener> visualizerConfigListeners;
	private static VisualizerConfig instance;
	
	private InputOutputTargetEnum inputOutputTarget = InputOutputTargetEnum.FILE_SIMULATION;
	private LocalizationModeEnum localizationMode = LocalizationModeEnum.NONE;	
	private List<VisualizerConfigItemEnum> changedConfigs;
	
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

}
