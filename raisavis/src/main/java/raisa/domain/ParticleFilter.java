package raisa.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import raisa.util.Vector2D;

public class ParticleFilter implements SampleListener {
	private WorldModel world;
	private List<Particle> particles;
	private List<Sample> samples = new ArrayList<Sample>();
	private List<ParticleFilterListener> particleFilterListeners = new ArrayList<ParticleFilterListener>();

	public ParticleFilter(WorldModel world, int nparticles) {
		this.world = world;
		randomizeParticles(nparticles);
	}

	public void randomizeParticles(int nparticles) {
		particles = new ArrayList<Particle>();
		// assume near origo
		float width = world.getWidth() * 0.05f;
		float height = world.getHeight() * 0.05f;
		for (int i = 0; i < nparticles; ++i) {
			Particle particle = new Particle();
			float x = (float) Math.random() * width - 0.5f * width;
			float y = (float) Math.random() * height - 0.5f * height;
			float heading = (float) Math.random() * (float) Math.PI * 2.0f;
			Robot robot = new Robot(new Vector2D(x, y), heading);
			particle.addState(robot);
			particles.add(particle);
		}
	}

	private void updateParticles(List<Sample> samples) {
		if (samples.isEmpty()) return;
		
		// estimate movement
		RobotMovementEstimator estimator = new SimpleRobotMovementEstimator();
		Sample lastSample = samples.get(samples.size() - 1);
		for (Particle particle : particles) {
			Robot newState = estimator.moveRobot(particle.getLastState(), lastSample);
			particle.addState(newState);
		}
		
		// calculate weights
		float totalWeights = 0.0f;
		Map<Particle, Float> weights = new LinkedHashMap<Particle, Float>();
		for (Particle particle : particles) {
			float weight = particle.calculateWeight(world, samples);
			weights.put(particle, weight);
			totalWeights += weight;
		}
		if (totalWeights > 0.0f) {
			// normalize weights
			for (Particle particle : particles) {
				weights.put(particle, weights.get(particle) / totalWeights);
			}
			// sample new particles with replacement
			List<Particle> newParticles = new ArrayList<Particle>();

			for (int i = 0; i < particles.size(); ++i) {
				float r = (float) Math.random();
				float s = 0.0f;
				Particle selectedParticle = null;
				for (Particle particle : weights.keySet()) {
					float weight = weights.get(particle);
					s += weight;
					if (r <= s) {
						selectedParticle = particle;
						break;
					}
				}
				if (selectedParticle != null) {
					newParticles.add(selectedParticle.copy());
				}
			}

			this.particles = newParticles;
			notifyParticleFilterListeners();
		}
	}

	private void notifyParticleFilterListeners() {
		for (ParticleFilterListener listener : particleFilterListeners) {
			listener.particlesChanged(this);
		}
	}

	public List<Particle> getParticles() {
		return particles;
	}

	@Override
	public void sampleAdded(Sample sample) {
		samples.add(sample);
		updateParticles(samples);
	}

	public void addParticleFilterListener(ParticleFilterListener listener) {
		particleFilterListeners.add(listener);
	}
}
