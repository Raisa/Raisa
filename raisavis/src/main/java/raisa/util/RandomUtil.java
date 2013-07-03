package raisa.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Utility that contains all random generators and distributions. 
 * Allows setting fixed random seed. By default uses current time as seed.
 */
public final class RandomUtil {
	private static long seed;
	private static Random random;
	private static Random randomSeeder;
	
	static {
		setSeed(System.currentTimeMillis());
	}

	public static void setSeed(long seed) {
		RandomUtil.seed = seed;
		RandomUtil.randomSeeder = new Random(seed);
		RandomUtil.random = new Random(randomSeeder.nextLong());
	}
	
	/**
	 * Get current seed.
	 */
	public static long getSeed() {
		return seed;
	}
	
	/** Range [0.0f, 1.0f[ */
	public static float nextFloat() {
		return random.nextFloat();
	}

	/** Range [0.0, 1.0[ */
	public static double random() {
		return nextDouble();
	}
	
	/** Range [0.0, 1.0[ */
	public static double nextDouble() {
		return random.nextDouble();
	}
	
	/** Gaussian: mean 0.0, stdev 1.0 */
	public static double nextGaussian() {
		return random.nextGaussian();
	}
	
	/**
	 * setSeed() doesn't affect already existing distribution instances.
	 */
	public static NormalDistribution normalDistribution(double mean, double sd) {
		NormalDistribution dist = new NormalDistribution(mean, sd);
		dist.reseedRandomGenerator(seed);
		return dist;
	}
	
	/**
	 * Shuffle list in-place.
	 */
	public static void shuffle(List<?> list) {
		Collections.shuffle(list, random);
	}
}
