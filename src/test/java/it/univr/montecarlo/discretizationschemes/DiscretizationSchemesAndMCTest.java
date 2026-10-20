package it.univr.montecarlo.discretizationschemes;


import it.univr.usefulmethodsforarrays.UsefulMethodsForArrays;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;


/**
 * In this class we test the discretization schemes that we have implemented. The underlying
 * is a Black-Scholes model. We compute expectation and variance of samples, for repeated
 * tests: we have then the combination of both discretization error and Monte-Carlo error.
 */
public class DiscretizationSchemesAndMCTest {

	
	public static void main(String[] args) {

		double initialValue = 100.0;
		double volatility = 0.3;
		double muDrift = 0.1;
		
		//here we see how to construct an object of type TimeDiscretization
		double finalTime = 1.0;
		double timeStep = 0.05;
		int numberOfTimesSteps = (int) (finalTime/timeStep);

		TimeDiscretization times = new TimeDiscretizationFromArray(0.0, numberOfTimesSteps, timeStep);

		//for the logarithm, one time step is enough: we simulate the exact solution!
		TimeDiscretization timesForLogarithm = new TimeDiscretizationFromArray(0.0, 1, finalTime);

		//this is how much we save from the time discretization for the simulation of the logarithm
		int ratioBetweenNumberOfTimeSteps = times.getNumberOfTimeSteps()/timesForLogarithm.getNumberOfTimeSteps();

		int numberOfSimulatedPaths = 100000;
		
		//we use the "saving" ratioBetweenNumberOfTimeSteps we get by simulating less times to simulate more paths
		int numberOfSimulatedPathsForLogarithm = numberOfSimulatedPaths*ratioBetweenNumberOfTimeSteps;

		
		/*
		 * Each "test" corresponds to a different value for expectation and variance, for each
		 * discretization method: indeed, for each "test" we simulate paths with a different seed.
		 * So, for each discretization method we will have 200 values for the expectation and 200
		 * value for the variance. We will then look at the error woth respect to the analytic
		 * expectation and the analytic variance, respectively.
		 */
		int numberOfTests = 200;

		/*
		 * Arrays that will contain all the percentage errors with respect to the analytic expectation
		 * for each discretization method
		 */
		double[] percentageErrorsEulerMaruyama = new double[numberOfTests];
		double[] percentageErrorsMilstein = new double[numberOfTests];
		double[] percentageErrorsPredictorCorrector= new double[numberOfTests];
		double[] percentageErrorsEulerMaruyamaForLogarithm = new double[numberOfTests];
		
		/*
		 * Arrays that will contain all the percentage errors (in absolute value) with respect to the
		 * analytic variance for each discretization method
		 */
		double[] percentageAbsoluteErrorsVarianceEulerMaruyama = new double[numberOfTests];
		double[] percentageAbsoluteErrorsVarianceMilstein = new double[numberOfTests];
		double[] percentageAbsoluteErrorsVariancePredictorCorrector= new double[numberOfTests];
		double[] percentageAbsoluteErrorsVarianceEulerMaruyamaForLogarithm = new double[numberOfTests];

		//analytic expectation for Black-Scholes model
		double analyticExpectation = initialValue * Math.exp(finalTime*muDrift);
		
		//analytic variance for Black-Scholes model
		double analyticVariance = initialValue*initialValue * Math.exp(2*finalTime*muDrift)
									*(Math.exp(finalTime*volatility*volatility)-1);
		
		//at each iteration of the algorithm, we simulate paths with a different seed. 	
		for (int i = 0; i<numberOfTests; i++) {

			//different seed for each value of i
			int seed = 10*i;

			
			//all approximation methods
			AbstractProcessSimulation simulatorEulerMaruyama = new EulerSchemeForBlackScholes(muDrift, volatility, initialValue,
					numberOfSimulatedPaths, seed, times);

			AbstractProcessSimulation simulatorMilstein = new MilsteinSchemeForBlackScholes(muDrift, volatility,  initialValue,
					numberOfSimulatedPaths, seed, times);
			
			
			AbstractProcessSimulation simulatorLogEuler = new LogEulerSchemeForBlackScholes(muDrift, volatility, initialValue,
					numberOfSimulatedPathsForLogarithm, seed, timesForLogarithm);

			
			AbstractProcessSimulation simulatorPredictorCorrector = new PredictorCorrectorForBlackScholes(muDrift, volatility, initialValue,
					numberOfSimulatedPaths, seed, times);
			

			//We will modify these lines together
			double expectationEulerMaruyama = simulatorEulerMaruyama.getFinalValue().getAverage();
			double expectationMilstein = simulatorMilstein.getFinalValue().getAverage();
			double expectationPredictorCorrector = simulatorPredictorCorrector.getFinalValue().getAverage();
			double expectationEulerMaruyamaForLogarithm = simulatorLogEuler.getFinalValue().getAverage();
			
			double varianceEulerMaruyama = simulatorEulerMaruyama.getFinalValue().getVariance();
			double varianceMilstein = simulatorMilstein.getFinalValue().getVariance();
			double variancePredictorCorrector = simulatorPredictorCorrector.getFinalValue().getVariance();
			double varianceEulerMaruyamaForLogarithm = simulatorLogEuler.getFinalValue().getVariance();
			
			
			percentageErrorsEulerMaruyama[i] =
					(expectationEulerMaruyama-analyticExpectation)/analyticExpectation*100; 
			percentageErrorsMilstein[i] = 
					(expectationMilstein-analyticExpectation)/analyticExpectation*100; 
			percentageErrorsPredictorCorrector[i] = 
					(expectationPredictorCorrector-analyticExpectation)/analyticExpectation*100; 
			percentageErrorsEulerMaruyamaForLogarithm[i] = 
					(expectationEulerMaruyamaForLogarithm-analyticExpectation)/analyticExpectation*100; 
			
			//We will modify these lines together
			percentageAbsoluteErrorsVarianceEulerMaruyama[i] =
					Math.abs(varianceEulerMaruyama-analyticVariance)/analyticVariance*100; 
			percentageAbsoluteErrorsVarianceMilstein[i] = 
					Math.abs(varianceMilstein-analyticVariance)/analyticVariance*100; 
			percentageAbsoluteErrorsVariancePredictorCorrector[i] = 
					Math.abs(variancePredictorCorrector-analyticVariance)/analyticVariance*100; 
			percentageAbsoluteErrorsVarianceEulerMaruyamaForLogarithm[i] = 
					Math.abs(varianceEulerMaruyamaForLogarithm-analyticVariance)/analyticVariance*100; 
		}

		System.out.println("Average percentage error Euler Maruyama for expected value: "
				+ UsefulMethodsForArrays.getAverage(percentageErrorsEulerMaruyama));
		System.out.println("Average percentage error Milstein for expected value: "
					+ UsefulMethodsForArrays.getAverage(percentageErrorsMilstein));
		System.out.println("Average percentage error Predictor-Corrector for expected value: "
				+ UsefulMethodsForArrays.getAverage(percentageErrorsPredictorCorrector));
		System.out.println("Average percentage error Log Euler Maruyama for expected value: "
						+ UsefulMethodsForArrays.getAverage(percentageErrorsEulerMaruyamaForLogarithm));
		
		System.out.println();
		
		System.out.println("Average percentage absolute error Euler Maruyama for variance: "
				+ UsefulMethodsForArrays.getAverage(percentageAbsoluteErrorsVarianceEulerMaruyama));
		System.out.println("Average percentage absolute error Milstein for variance: "
					+ UsefulMethodsForArrays.getAverage(percentageAbsoluteErrorsVarianceMilstein));
		System.out.println("Average percentage absolute error Predictor-Corrector for variance: "
				+ UsefulMethodsForArrays.getAverage(percentageAbsoluteErrorsVariancePredictorCorrector));
		System.out.println("Average percentage absolute error Log Euler Maruyama for variance: "
						+ UsefulMethodsForArrays.getAverage(percentageAbsoluteErrorsVarianceEulerMaruyamaForLogarithm));
	
	}
}
