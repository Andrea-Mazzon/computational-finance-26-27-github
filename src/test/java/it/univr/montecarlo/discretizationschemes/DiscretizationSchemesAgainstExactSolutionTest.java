package it.univr.montecarlo.discretizationschemes;

import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * In this class, we focus on the difference between approximated and exact solution
 * of the Black-Scholes SDE. The exact solution is provided, as a benchmark, by the
 * class LogEulerSchemeForBlackScholes.
 */
public class DiscretizationSchemesAgainstExactSolutionTest {

    public static void main(String[] args) {

    	//model parameters
        final double initialValue = 100.0;
        final double volatility = 0.3;
        final double muDrift = 0.1;
        final double finalTime = 1.0;

        //discretization and simulation parameters
        final int numberOfPaths = 10000;
        final int seed = 1897;

        final int[] numbersOfTimeSteps = {5,10,20,40,80,160,320, 640};

        System.out.println("MEAN ABSOLUTE PATHWISE ERROR AT FINAL TIME (%)");
        System.out.printf("%10s %16s %16s%n",
                "Time step", "Euler", "Milstein");

        /*
         * For each time step, we compare the approximate and exact final values
         * on each simulated path. We print the mean absolute percentage error across paths.
         */
        for (int numberOfTimeSteps : numbersOfTimeSteps) {

        	//the length of the time step
            final double timeStep = finalTime / numberOfTimeSteps;

            final TimeDiscretization times = new TimeDiscretizationFromArray(
                            0.0, numberOfTimeSteps, timeStep);

            
            //this provides the exact solution
            final AbstractProcessSimulation exact =
                    new LogEulerSchemeForBlackScholes(muDrift, volatility, initialValue,
                            numberOfPaths, seed, times);
            
            //here we have a discretization error
            final AbstractProcessSimulation euler =
                    new EulerSchemeForBlackScholes(muDrift, volatility, initialValue,
                            numberOfPaths, seed, times);

          //here we have a discretization error
            final AbstractProcessSimulation milstein =
                    new MilsteinSchemeForBlackScholes(muDrift, volatility, initialValue,
                            numberOfPaths, seed, times);


            RandomVariable finalValuesExact = exact.getFinalValue();
            RandomVariable finalValuesEuler = euler.getFinalValue();
            RandomVariable finalValuesMilstein = milstein.getFinalValue();
                        
             // We compare sample means computed from the same Brownian increments. 
            
          //We will modify these lines together
            final double eulerPercentageError =
                    100.0 * 0.0;

            final double milsteinPercentageError =
            		100.0 * 0.0;


            System.out.printf("%10.5f %15.4f%% %15.4f%%%n", timeStep,
            		eulerPercentageError, milsteinPercentageError);
        }
    }
}