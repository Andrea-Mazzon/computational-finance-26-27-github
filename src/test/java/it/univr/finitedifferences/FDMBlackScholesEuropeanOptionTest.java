package it.univr.finitedifferences;

import java.util.Locale;

import net.finmath.finitedifference.assetderivativevaluation.models.FDMBlackScholesModel;
import net.finmath.finitedifference.assetderivativevaluation.products.BarrierOption;
import net.finmath.finitedifference.assetderivativevaluation.products.EuropeanOption;
import net.finmath.finitedifference.grids.Grid;
import net.finmath.finitedifference.grids.SpaceTimeDiscretization;
import net.finmath.finitedifference.grids.UniformGrid;
import net.finmath.functions.AnalyticFormulas;

import net.finmath.modelling.products.CallOrPut;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;




public class FDMBlackScholesEuropeanOptionTest {

    // Model parameters
    private static final double RISK_FREE_RATE = 0.06;
    private static final double DIVIDEND_YIELD = 0.0;
    private static final double VOLATILITY = 0.4;
    private static final double INITIAL_VALUE = 50.0;

    // Discretization parameters
    private static final int NUMBER_SPACE_STEPS = 120;
    private static final int NUMBER_TIME_STEPS = 120;
    private static final double THETA = 0.5;

    // Option parameters
    private static final double STRIKE = 50.0;
    private static final double MATURITY = 1.0;
    
 

    // Values used in the numerical experiments
    private static final double[] TIMES =
            {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    private static final double[] UNDERLYING_VALUES =
            {30, 35, 40, 45, 50, 55, 60, 65, 70};

    private static final double PLOT_MIN_UNDERLYING = 30.0;
    private static final double PLOT_MAX_UNDERLYING = 70.0;

    //FROM HERE ON, WE WILL WRITE THE CODE TOGETHER
}
