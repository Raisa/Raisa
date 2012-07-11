package raisa.domain;

import java.util.ArrayList;
import java.util.List;

import raisa.util.Vector2D;

public class ParticleFilter {
	private List<Particle> particles;
	
	public ParticleFilter(WorldModel world, int nparticles) {
		randomizeParticles(world, nparticles);
	}

	public void randomizeParticles(WorldModel world, int nparticles) {
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
	
	public List<Particle> getParticles() {
		return particles;
	}
}
