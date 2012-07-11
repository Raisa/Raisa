package raisa.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import raisa.util.Vector2D;

public class ParticleFilter {
	private WorldModel world;
	private List<Particle> particles;
	
	public ParticleFilter(WorldModel world, int nparticles) {
		this.world = world;
		randomizeParticles(nparticles);
	}

	public void randomizeParticles(int nparticles) {
		particles =  new ArrayList<Particle>();
		float width = world.getWidth();
		float height = world.getHeight();		
		for (int i = 0; i < nparticles; ++i) {
			Particle particle = new Particle();
			float x = (float)Math.random() * width - 0.5f * width;
			float y = (float)Math.random() * height - 0.5f * height;
			float heading = (float)Math.random() * (float)Math.PI * 2.0f;
			Robot robot = new Robot(new Vector2D(x, y), heading);
			particle.addState(robot);
			particles.add(particle);
		}
	}

	public void update(List<Sample> samples) {
		float totalWeights = 0.0f;
		Map<Particle, Float> weights = new LinkedHashMap<Particle, Float>();
		// calculate weights
		for (Particle particle : particles) {
			float weight = particle.calculateWeight(world, samples);
			weights.put(particle, weight);
			totalWeights += weight;
		}
		// normalize weights
		for (Particle particle : particles) {
			weights.put(particle, weights.get(particle) / totalWeights);
		}
		// sample new particles with replacement
		List<Particle> newParticles = new ArrayList<Particle>();
		
		for (int i = 0; i < particles.size(); ++i) {			
			float r = (float)Math.random();
			float s = 0.0f;
			for (Particle particle : weights.keySet()) {
				float weight = weights.get(particle);
				s += weight;
				if (r <= s) {
					newParticles.add(particle);
				}
			}
		}
		
		this.particles = newParticles;
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
}
