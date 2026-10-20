package it.univr.montecarlo.discretizationschemes;

import net.finmath.montecarlo.BrownianMotion;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;

/**
 * This class simulates the trajectories of a geometric Brownian motion (i.e.,
 * Black-Scholes model) by using a predictor-corrector scheme. It extends AbstractSimulation
 * by giving the implementation of getDrift and getDiffusion.
 *
 * @author Andrea Mazzon
 */
public class PredictorCorrectorForBlackScholes extends AbstractProcessSimulation {

	private final double muDrift;// mu
	private final double sigmaVolatility;// sigma

	/**
	 * It constructs an object of type PredictorCorrectorForBlackScholes, which provides
	 * the Euler scheme for the Black-Scholes model.
	 * 
	 * @param sigmaVolatility: the volatility sigma in dX_t = mu X_t dt + sigma X_t dW_t
	 * @param muDrift: the drift mu in dX_t = mu X_t dt + sigma X_t dW_t
	 * @param initialValue: X_0
	 * @param numberOfSimulations: the number of simulated trajectories 
	 * @param seed: the seed to simulate the trajectories of the process
	 * @param times: the time discretization where we simulate the process
	 */
	public PredictorCorrectorForBlackScholes(double muDrift, double sigmaVolatility, 
			double initialValue, int numberOfSimulations, int seed, TimeDiscretization times) {
		super(initialValue, numberOfSimulations, seed, times);
		this.muDrift = muDrift;
		this.sigmaVolatility = sigmaVolatility;
		this.transform = (x -> x);
		this.inverseTransform = (x -> x);
	}

	/*
	 * It gets and returns the drift of a geometric Brownian motion computed with
	 * the predictor-corrector scheme. 
	 */
	@Override
	protected RandomVariable getDrift(RandomVariable lastRealization, int timeIndex) {
		TimeDiscretization times = getTimeDiscretization();
		final double timeStep = times.getTimeStep(timeIndex - 1);
		RandomVariable predictor = lastRealization.add(lastRealization.mult(muDrift).mult(timeStep))
				.add(getDiffusion(lastRealization, timeIndex));
		return lastRealization.add(predictor).mult(0.5*muDrift*timeStep);
	}

	/*
	 * It gets and returns the diffusion of a geometric Brownian motion computed
	 * with the predictor-corrector. 
	 */
	@Override
	protected RandomVariable getDiffusion(RandomVariable lastRealization, int timeIndex) {
		/*
		 * Note that in the super class brownianMotion is private, but
		 * getStochasticDriver() is public (it could also be protected)
		 */
		BrownianMotion brownianMotion = getStochasticDriver();
		final RandomVariable brownianIncrement = brownianMotion.getBrownianIncrement(timeIndex - 1, 0);
		return lastRealization.mult(sigmaVolatility).mult(brownianIncrement);
	}

}
