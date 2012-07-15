package raisa;

import java.util.ArrayList;
import java.util.List;

public class VisualizerConfig {
	
	private List<VisualizerConfigListener> visualizerConfigListeners;
	private static VisualizerConfig instance;
	
	private boolean particleFilterEnabled = false;
	
	private VisualizerConfig() {
		this.visualizerConfigListeners = new ArrayList<VisualizerConfigListener>();
	}
	
	public static VisualizerConfig getInstance() {
		if (instance == null) {
			instance = new VisualizerConfig();
		}
		return instance;
	}
	
	public boolean isParticleFilterEnabled() {
		return particleFilterEnabled;
	}

	public void setParticleFilterEnabled(boolean enabled) {
		particleFilterEnabled = enabled;
	}	
	
	public void addVisualizerConfigListener(VisualizerConfigListener listener) {
		visualizerConfigListeners.add(listener);
	}
		
	public void notifyVisualizerConfigListeners() {
		for (VisualizerConfigListener listener : visualizerConfigListeners) {
			listener.visualizerConfigChanged(this);
		}
	}

}
