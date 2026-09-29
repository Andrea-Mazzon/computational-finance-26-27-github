package it.univr.montecarlo.variancereduction.antitheticvariables;


import it.univr.usefulmethodsforarrays.UsefulMethodsForArrays;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * In this class we compare the variability of European call prices obtained with
 * ordinary and antithetic Brownian increments.
 */
public class AntitheticVariablesEuropeanOptionTest {

    public static void main(String[] args) throws CalculationException {

        // Option parameters
        final double maturity = 2.0;
        final double strike = 100.0;
        final EuropeanOption option = new EuropeanOption(maturity, strike);

        // Model parameters
        final double initialValue = 100.0;
        final double riskFreeRate = 0.05;
        final double volatility = 0.2;

        // Time discretization
        final double timeStep = 0.1;
        final int numberOfTimeSteps = (int) (maturity / timeStep);
        final TimeDiscretization times = new TimeDiscretizationFromArray(
                0.0, numberOfTimeSteps, timeStep);

        // Use the same total number of paths in both methods.
        final int numberOfPaths = 10000;
        final int numberOfTests = 100;

        final double[] ordinaryPrices = new double[numberOfTests];
        final double[] antitheticPrices = new double[numberOfTests];

        /*
         * For each test, we get a different price, and we store it in an array.
         * We will then print average and variance of this array for ordinary
         * Monte-Carlo computation and Antitethic variables one.
         */
        for (int testIndex = 0; testIndex < numberOfTests; testIndex++) {

            final int seed = testIndex*10;

            final BrownianMotion ordinaryDriver =
                    new BrownianMotionFromMersenneRandomNumbers(
                            times, 1, numberOfPaths, seed);

            //an object of our class for antithetic variables
            final BrownianMotion antitheticDriver =
                    new BrownianMotionFromMersenneRandomNumbersAntitheticVariables(
                            times, 1, numberOfPaths, seed);

            
            //then we construct the simulations
            final MonteCarloBlackScholesModel ordinaryModel =
                    new MonteCarloBlackScholesModel(
                            initialValue, riskFreeRate, volatility,
                            ordinaryDriver);

            final MonteCarloBlackScholesModel antitheticModel =
                    new MonteCarloBlackScholesModel(
                            initialValue, riskFreeRate, volatility,
                            antitheticDriver);

  
            ordinaryPrices[testIndex] = option.getValue(ordinaryModel);
            antitheticPrices[testIndex] = option.getValue(antitheticModel);
        }

        final double ordinaryVariance = UsefulMethodsForArrays.getVariance(ordinaryPrices);
        final double antitheticVariance = UsefulMethodsForArrays.getVariance(antitheticPrices);


        System.out.printf("%-25s %16s %16s%n",
                "", "Mean price", "Price variance");
        System.out.printf("%-25s %16.6f %16.8f%n",
                "Ordinary Monte Carlo",
                UsefulMethodsForArrays.getAverage(ordinaryPrices), ordinaryVariance);
        System.out.printf("%-25s %16.6f %16.8f%n",
                "Antithetic variables",
                UsefulMethodsForArrays.getAverage(antitheticPrices), antitheticVariance);

        System.out.printf("%nVariance ratio (ordinary / antithetic): %.3f%n",
                ordinaryVariance / antitheticVariance);
    }

  
}
