/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 09.02.2004
 */
package it.univr.montecarlo.variancereduction.antitheticvariables;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.Validate;

import net.finmath.functions.NormalDistribution;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.RandomVariableFactory;
import net.finmath.montecarlo.RandomVariableFromArrayFactory;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;

/**
 * This is basically a copy of the Finmath Library class BrownianMotionFromMersenneRandomNumbers, 
 * where half of the Brownian trajectories have increments which are the opposite of the incremented
 * of the other two half of trajectories: this is used to implement antithetic variables.
 */
public class BrownianMotionFromMersenneRandomNumbersAntitheticVariables implements BrownianMotion, Serializable {

	private static final long serialVersionUID = -5430067621669213475L;

	private final TimeDiscretization						timeDiscretization;

	private final int			numberOfFactors;
	private final int			numberOfPaths;
	private final int			seed;

	private final RandomVariableFactory randomVariableFactory;

	private transient	RandomVariable[][]	brownianIncrements;
	private transient 	Object				brownianIncrementsLazyInitLock = new Object();

	/**
	 * Construct a Brownian motion for antithetic variables.
	 *
	 * The constructor allows to set the factory to be used for the construction of
	 * random variables. This allows to generate Brownian increments represented
	 * by different implementations of the RandomVariable (e.g. the RandomVariableFromFloatArray internally
	 * using float representations).
	 *
	 * @param timeDiscretization The time discretization used for the Brownian increments.
	 * @param numberOfFactors Number of factors.
	 * @param numberOfPaths Number of paths to simulate.
	 * @param seed The seed of the random number generator.
	 * @param randomVariableFactory Factory to be used to create random variable.
	 */
	public BrownianMotionFromMersenneRandomNumbersAntitheticVariables(
			final TimeDiscretization timeDiscretization,
			final int numberOfFactors,
			final int numberOfPaths,
			final int seed,
			final RandomVariableFactory randomVariableFactory) {
		super();
		Validate.isTrue(numberOfFactors > 0, "Number of factors must be greater or equal 1 (given %d).", numberOfFactors);
		Validate.isTrue(numberOfPaths > 0, "Number of paths must be greater or equal 1 (given %d).", numberOfPaths);

		this.timeDiscretization = timeDiscretization;
		this.numberOfFactors	= numberOfFactors;
		this.numberOfPaths		= numberOfPaths;
		this.seed				= seed;

		this.randomVariableFactory = randomVariableFactory;

		brownianIncrements	= null; 	// Lazy initialization
	}

	/**
	 * Construct a Brownian motion for antithetic variables.
	 *
	 * @param timeDiscretization The time discretization used for the Brownian increments.
	 * @param numberOfFactors Number of factors.
	 * @param numberOfPaths Number of paths to simulate.
	 * @param seed The seed of the random number generator.
	 */
	public BrownianMotionFromMersenneRandomNumbersAntitheticVariables(
			final TimeDiscretization timeDiscretization,
			final int numberOfFactors,
			final int numberOfPaths,
			final int seed) {
		this(timeDiscretization, numberOfFactors, numberOfPaths, seed, new RandomVariableFromArrayFactory());
	}

	@Override
	public BrownianMotion getCloneWithModifiedSeed(final int seed) {
		return new BrownianMotionFromMersenneRandomNumbersAntitheticVariables(getTimeDiscretization(), getNumberOfFactors(), getNumberOfPaths(), seed);
	}

	@Override
	public BrownianMotion getCloneWithModifiedTimeDiscretization(final TimeDiscretization newTimeDiscretization) {
		/// @TODO This can be improved: a complete recreation of the Brownian motion wouldn't be necessary!
		return new BrownianMotionFromMersenneRandomNumbersAntitheticVariables(newTimeDiscretization, getNumberOfFactors(), getNumberOfPaths(), getSeed());
	}

	@Override
	public RandomVariable getIncrement(final int timeIndex, final int factor) {
		return getBrownianIncrement(timeIndex, factor);
	}

	@Override
	public RandomVariable getBrownianIncrement(final int timeIndex, final int factor) {

		// Thread safe lazy initialization
		synchronized(brownianIncrementsLazyInitLock) {
			if(brownianIncrements == null) {
				doGenerateBrownianMotion();
			}
		}

		/*
		 *  We return an immutable object which ensures that the receiver does not alter the data.
		 */
		return brownianIncrements[timeIndex][factor];
	}

	/**
	 * Lazy initialization of brownianIncrement. Synchronized to ensure thread safety of lazy init.
	 */
	private void doGenerateBrownianMotion() {
		if(brownianIncrements != null) {
			return;	// Nothing to do
		}

		// Create random number sequence generator
		final MersenneTwister mersenneTwister = new MersenneTwister(seed);

		// Allocate memory
		final double[][][] brownianIncrementsArray = new double[timeDiscretization.getNumberOfTimeSteps()][numberOfFactors][numberOfPaths];

		// Pre-calculate square roots of deltaT
		final double[] sqrtOfTimeStep = new double[timeDiscretization.getNumberOfTimeSteps()];
		for(int timeIndex=0; timeIndex<sqrtOfTimeStep.length; timeIndex++) {
			sqrtOfTimeStep[timeIndex] = Math.sqrt(timeDiscretization.getTimeStep(timeIndex));
		}

		if (numberOfPaths % 2 != 0) {
		    throw new IllegalArgumentException(
		            "Antithetic simulation requires an even number of paths.");
		}

		/*
		 * Paths 2 * pair and 2 * pair + 1 form an antithetic pair.
		 * At every time step and for every factor, their Brownian
		 * increments have opposite signs.
		 */
		for (int pair = 0; pair < numberOfPaths / 2; pair++) {

		    final int firstPath = 2 * pair;
		    final int antitheticPath = firstPath + 1;

		    for (int timeIndex = 0;
		            timeIndex < timeDiscretization.getNumberOfTimeSteps();
		            timeIndex++) {

		        final double sqrtDeltaT = sqrtOfTimeStep[timeIndex];

		        for (int factor = 0; factor < numberOfFactors; factor++) {

		            final double uniformIncrement =
		                    mersenneTwister.nextDoubleFast();

		            final double increment =
		                    NormalDistribution.inverseCumulativeDistribution(
		                            uniformIncrement) * sqrtDeltaT;

		            brownianIncrementsArray[timeIndex][factor][firstPath] =
		                    increment;

		            brownianIncrementsArray[timeIndex][factor][antitheticPath] =
		                    -increment;
		        }
		    }
		}

		// Allocate memory for RandomVariableFromDoubleArray wrapper objects.
		brownianIncrements = new RandomVariable[timeDiscretization.getNumberOfTimeSteps()][numberOfFactors];

		// Wrap the values in RandomVariableFromDoubleArray objects
		for(int timeIndex=0; timeIndex<timeDiscretization.getNumberOfTimeSteps(); timeIndex++) {
			final double time = timeDiscretization.getTime(timeIndex+1);
			for(int factor=0; factor<numberOfFactors; factor++) {
				brownianIncrements[timeIndex][factor] =
						randomVariableFactory.createRandomVariable(time, brownianIncrementsArray[timeIndex][factor]);
			}
		}
	}

	@Override
	public TimeDiscretization getTimeDiscretization() {
		return timeDiscretization;
	}

	@Override
	public int getNumberOfFactors() {
		return numberOfFactors;
	}

	@Override
	public int getNumberOfPaths() {
		return numberOfPaths;
	}

	@Override
	public RandomVariable getRandomVariableForConstant(final double value) {
		return randomVariableFactory.createRandomVariable(value);
	}

	/**
	 * @return Returns the seed.
	 */
	public int getSeed() {
		return seed;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [timeDiscretization=" + timeDiscretization + ", numberOfFactors="
				+ numberOfFactors + ", numberOfPaths=" + numberOfPaths + ", seed=" + seed
				+ ", randomVariableFactory=" + randomVariableFactory + "]";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final BrownianMotionFromMersenneRandomNumbersAntitheticVariables that = (BrownianMotionFromMersenneRandomNumbersAntitheticVariables) o;

		if ((numberOfFactors != that.numberOfFactors) || (numberOfPaths != that.numberOfPaths) || (seed != that.seed)) {
			return false;
		}
		return timeDiscretization.equals(that.timeDiscretization);
	}

	@Override
	public int hashCode() {
		int result = timeDiscretization.hashCode();
		result = 31 * result + numberOfFactors;
		result = 31 * result + numberOfPaths;
		result = 31 * result + seed;
		return result;
	}

	private void readObject(final java.io.ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		// initialization of transients
		brownianIncrementsLazyInitLock = new Object();
	}
}