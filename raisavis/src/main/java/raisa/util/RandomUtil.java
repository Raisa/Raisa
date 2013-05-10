package raisa.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public static long getSeed() {
		return seed;
	}
	
	public static float nextFloat() {
		return random.nextFloat();
	}

	public static double random() {
		return nextDouble();
	}
	
	public static double nextDouble() {
		return random.nextDouble();
	}
	
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
	
	public static void shuffle(List<?> list) {
		Collections.shuffle(list, random);
	}
}
