package it.univr.finitedifferences;

import java.util.Locale;

import it.univr.analyticformulas.OurAnalyticFormulas;
import net.finmath.finitedifference.assetderivativevaluation.models.FDMBlackScholesModel;
import net.finmath.finitedifference.assetderivativevaluation.products.BarrierOption;
import net.finmath.finitedifference.grids.Grid;
import net.finmath.finitedifference.grids.SpaceTimeDiscretization;
import net.finmath.finitedifference.grids.UniformGrid;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class FDMBlackScholesBarrierOptionTest {
	 // Model parameters
    private static final double RISK_FREE_RATE = 0.06;
    private static final double DIVIDEND_YIELD = 0.0;
    private static final double VOLATILITY = 0.4;
    private static final double INITIAL_VALUE = 50.0;

    // Discretization parameters
    private static final int NUMBER_SPACE_STEPS = 480;
    private static final int NUMBER_TIME_STEPS = 480;
    private static final double THETA = 0.1;

    // Option parameters
    private static final double STRIKE = 50.0;
    private static final double MATURITY = 1.0;
    
    private static final double LOWERBARRIER = 40;

    // Values used in the numerical experiments
    private static final double[] TIMES =
            {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    private static final double[] UNDERLYING_VALUES =
            {40, 42.5, 45, 47.5, 50, 52.5, 55, 57.5, 60, 62.5, 65, 67.5, 70};

    private static final double PLOT_MIN_UNDERLYING = 40.0;
    private static final double PLOT_MAX_UNDERLYING = 70.0;

  //FROM HERE ON, WE WILL WRITE THE CODE TOGETHER
    
}
