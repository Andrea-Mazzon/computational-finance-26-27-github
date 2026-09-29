package it.univr.analyticformulas;

import net.finmath.functions.AnalyticFormulas;

/**
 * This class contains analytic formulas we will use
 */
public class OurAnalyticFormulas {


	/**
	 * Analytic valuation of a down-and-out barrier option written on Black-Scholes
	 * @param initialValue
	 * @param riskFreeRate
	 * @param sigma
	 * @param maturity
	 * @param strike
	 * @param lowerBarrier
	 * @return the analytic price
	 */
	public static double blackScholesDownAndOut(double initialValue, double riskFreeRate, double sigma, double maturity, double strike,
			double lowerBarrier) {
		return AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, sigma, maturity, strike) 
				- Math.pow(initialValue/lowerBarrier,-(2*riskFreeRate/(sigma*sigma) - 1)) 
				* AnalyticFormulas.blackScholesOptionValue(lowerBarrier*lowerBarrier/initialValue, riskFreeRate, sigma, maturity, strike);
	}

}
