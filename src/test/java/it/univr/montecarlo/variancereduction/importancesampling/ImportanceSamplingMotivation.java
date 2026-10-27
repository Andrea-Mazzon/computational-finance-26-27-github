package it.univr.montecarlo.variancereduction.importancesampling;


import org.jblas.util.Random;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;



/**
 * In this class we experiment possible problems when we want to price options far out of-the-money 
 *
 * @author Andrea Mazzon
 *
 */
public class ImportanceSamplingMotivation {

	public static void main(String[] args) throws CalculationException {

		//parameters for the option

		double maturity = 1.0;
		double strike = 100;

		//so we construct the option object
		AbstractAssetMonteCarloProduct ourOption = new EuropeanOption (maturity, strike);
		
		//time discretization parameters
		double initialTime = 0.0;
		double timeStep = 0.01;
		int numberOfTimeSteps = (int) (maturity/timeStep);
		
		TimeDiscretization times = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, timeStep);
		
		//simulation parameters
		int numberOfPaths = 100000;
		
		//the seed here changes at every run
		int seed = Random.nextInt(10000000);

		BrownianMotion ourDriver = new BrownianMotionFromMersenneRandomNumbers(times, 1 /* numberOfFactors */, numberOfPaths, seed);
		
		//model (i.e., underlying) parameters
		double initialValue = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		
		//we construct an object of type MonteCarloBlackScholesModel: it represents the simulation of a Black-Scholes process
		MonteCarloBlackScholesModel blackScholesProcess = new MonteCarloBlackScholesModel(initialValue, riskFreeRate, volatility, ourDriver);
		
		double monteCarloPrice = ourOption.getValue(blackScholesProcess);

		double analyticPrice = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike);

		
		System.out.println("The Monte Carlo price is: " + monteCarloPrice);
		System.out.println("The analytic price is: " + analyticPrice);
		
		double percentageError = (monteCarloPrice-analyticPrice)/analyticPrice*100.0;
		
		System.out.println("Percentage error: " + percentageError);
		
	}
}