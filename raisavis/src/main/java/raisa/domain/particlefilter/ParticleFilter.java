package raisa.domain.particlefilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import raisa.domain.WorldModel;
import raisa.domain.robot.RobotMovementEstimator;
import raisa.domain.robot.RobotState;
import raisa.domain.robot.SimpleRobotMovementEstimator;
import raisa.domain.samples.Sample;
import raisa.util.CollectionUtil;
import raisa.util.RandomUtil;
import raisa.util.Vector2D;

public class ParticleFilter {

	private final WorldModel world;
	private List<Particle> particles;
	private List<Sample> samples = new ArrayList<Sample>();

	public ParticleFilter(WorldModel world, int nparticles) {
		this.world = world;
		randomizeParticles(nparticles);
	}

	public synchronized void reset() {
		randomizeParticles(particles.size());
		samples = new ArrayList<Sample>();
	}

	public void randomizeParticles(int nparticles) {
		particles = new ArrayList<Particle>();
		for (int i = 0; i < nparticles; ++i) {
			Particle particle = makeRandomParticle();
			particles.add(particle);
		}
	}

	public Particle makeRandomParticle() {
		float width = world.getWidth();
		float height = world.getHeight();
		boolean ok = false;
		Particle particle = new Particle();
		while (!ok) {
			float x = (float) RandomUtil.random() * width - 0.5f * width;
			float y = (float) RandomUtil.random() * height - 0.5f * height;
			Vector2D position = new Vector2D(x, y);
			if (world.isClear(position)) {
				float heading = (float) RandomUtil.random() * (float) Math.PI * 2.0f;
				RobotState robot = new RobotState(position, heading);
				particle.addState(robot);
				ok = true;
			}
		}
		return particle;
	}

	private synchronized void updateParticles(List<Sample> samples) {
		if (samples.isEmpty()) {
			return;
		}

		// estimate movement
		RobotMovementEstimator estimator = new SimpleRobotMovementEstimator(true);
		Sample lastSample = samples.get(samples.size() - 1);
		for (Particle particle : particles) {
			RobotState newState = estimator.moveRobot(particle.getLastState(), lastSample);
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
			float maxWeight = 0.0f;
			for (Particle particle : particles) {
				float normalizedWeight = weights.get(particle) / totalWeights;
				weights.put(particle, normalizedWeight);
				if (maxWeight < normalizedWeight) {
					maxWeight = normalizedWeight;
				}
			}
			// sample new particles with replacement
			List<Particle> newParticles = new ArrayList<Particle>();

			//executeMakeResampling(weights, newParticles);
			executeThrunResampling(weights, maxWeight, newParticles);

			// add a few random particles to avoid local maxima
			for (int i = 0; i < particles.size() / 7; ++i) {
				newParticles.set(i, makeRandomParticle());
			}

			this.particles = newParticles;
		}
	}

//	private void executeMakeResampling(Map<Particle, Float> weights,
//			List<Particle> newParticles) {
//		for (int i = 0; i < particles.size(); ++i) {
//			float r = (float) RandomUtil.random();
//			float s = 0.0f;
//			Particle selectedParticle = null;
//			for (Particle particle : weights.keySet()) {
//				float weight = weights.get(particle);
//				s += weight;
//				if (r <= s) {
//					selectedParticle = particle;
//					break;
//				}
//			}
//			if (selectedParticle != null) {
//				newParticles.add(selectedParticle.copy());
//			}
//		}
//	}

	private void executeThrunResampling(Map<Particle, Float> weights,
			float maxWeight, List<Particle> newParticles) {
		if(particles.isEmpty()) {
			// this may happen when particle filter has just started
			return;
		}
		int index = (int)RandomUtil.random() * particles.size();
		float beta = 0.0f;
		for (int i = 0; i < particles.size(); i++) {
			beta += RandomUtil.random() * 2.0 * maxWeight;
			float weight = weights.get(particles.get(index));
			while (beta > weight) {
				beta -= weight;
				index = (index + 1) % particles.size();
				weight = weights.get(particles.get(index));
			}
			newParticles.add(particles.get(index).copy());
		}
	}

	public List<Particle> getParticles() {
		return particles;
	}

	public void updateParticles(Sample sample) {
		samples.add(sample);
		samples = CollectionUtil.takeLast(samples, 50);
		updateParticles(samples);
	}

}
