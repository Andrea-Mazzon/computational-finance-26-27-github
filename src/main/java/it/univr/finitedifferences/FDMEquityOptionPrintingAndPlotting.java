package it.univr.finitedifferences;

import it.univr.usefulmethodsforarrays.UsefulMethodsForArrays;
import net.finmath.finitedifference.assetderivativevaluation.models.FiniteDifferenceEquityModel;
import net.finmath.finitedifference.assetderivativevaluation.products.FiniteDifferenceEquityProduct;
import net.finmath.finitedifference.grids.Grid;
import net.finmath.finitedifference.grids.SpaceTimeDiscretization;
import net.finmath.plots.Plot2D;
import net.finmath.plots.Plotable2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.time.TimeDiscretization;

/**
 * Evaluates a finite-difference equity product on the space-time grid
 * supplied by its model.
 *
 * The class obtains the complete price array from the product and provides
 * methods to access selected prices, print them, and display their evolution
 * in a plot. The discretization and price array are obtained when first
 * needed and then stored for subsequent calls.
 *
 * Where a requested underlying value is not on the spatial grid, the
 * nearest grid value is used. Where a requested time is not on the temporal
 * grid, the greatest grid time less than or equal to it is used.
 * 
 * Note that on the temporal grid, "time" is intended to be "time to maturity".
 */
public class FDMEquityOptionPrintingAndPlotting {

	/*
	 * The equity model specifies the dynamics of the underlying asset
	 * and provides the space-time discretization used for pricing.
	 * It is given in the constructor.
	 */
	private FiniteDifferenceEquityModel model;

	/*
	 * The product specifies the option to be priced, including its payoff
	 * and any relevant exercise or barrier conditions.
	 * It is given in the constructor.
	 */
	private FiniteDifferenceEquityProduct product;

	/*
	 * Stores the prices computed by the finite-difference method.
	 * finiteDifferencePrices[i][j] is the option price at the underlying
	 * value spaceGrid.getGrid()[i] and the time to maturity timeDiscretization.getTime(j).
	 */
	private double[][] finiteDifferencePrices;

	/*
	 * The complete discretization supplied by the model. It contains
	 * both the temporal grid and the spatial grid.
	 */
	private SpaceTimeDiscretization spaceTimeDiscretization;

	/*
	 * It contains the times to maturity at which the option prices are computed
	 * by the finite difference method.
	 */
	private TimeDiscretization timeDiscretization;

	/*
	 * It contains the underlying values at which the option prices are computed
	 * by the finite difference method.
	 */
	private Grid spaceGrid;

	/*
	 * Number of digits after the decimal point when printing times to maturity
	 * and underlying values.
	 */
	private static final int TIME_AND_UNDERLYING_DECIMALS = 2;

	
	// Number of digits after the decimal point when printing option prices.
	private static final int PRICE_DECIMALS = 4;
	
	/*
	 * Time in milliseconds for which each price curve remains visible
	 * before the plot is updated with the curve for the next requested time
	 * to maturity.
	 */
	private static final long PLOT_DELAY_MILLIS = 1000;

    /**
     * Creates an evaluator for the specified finite-difference model and
     * equity product.
     *
     * @param model the model supplying the space-time discretization
     * @param product the product whose prices are to be evaluated
     */
    public FDMEquityOptionPrintingAndPlotting(FiniteDifferenceEquityModel model,
    		FiniteDifferenceEquityProduct product) {
        this.model = model;
        this.product = product;
    }


    /*
     * This is called to generate the prices calculated by the finite difference
     * method. It gets called only once, the first time the prices are needed in
     * some public method.
     */
    private void generatePrices() {
    	//the specific method of the interface FiniteDifferenceProduct
        finiteDifferencePrices = product.getValues(model);
    }

    /*
     * This is called to initialize the space and time discretization.
     * It gets called only once, the first time the space and time discretizations
     * are needed in some public method.
     */
    private void generateSpaceTimeDiscretization() {
    	//it is "contained" in the model specification
        spaceTimeDiscretization = model.getSpaceTimeDiscretization();
    }

    /*
     * This is called to initialize the time discretization.
     * It gets called only once, the first time the time discretization alone
     * is needed in some public method.
     */
    private void generateTimeDiscretization() {
    	//first we generate the SpaceTimeDiscretization, if needed..
        if (spaceTimeDiscretization== null) {
            generateSpaceTimeDiscretization();
        }
        //..and then we get the time one
        timeDiscretization =  spaceTimeDiscretization.getTimeDiscretization();
    }

    /*
     * This is called to initialize the space discretization.
     * It gets called only once, the first time the space discretization alone
     * is needed in some public method.
     */
    private void generateSpaceGrid() {
    	//first we generate the SpaceTimeDiscretization, if needed..
        if (spaceTimeDiscretization== null) {
            generateSpaceTimeDiscretization();
        }
      //..and then we get the space one
        spaceGrid =  spaceTimeDiscretization.getSpaceGrid(0);
    }

    /**
     * Returns the complete array of finite-difference prices.
     *
     * The entry prices[i][j] is the option price at the
     * underlying value spaceGrid.getGrid()[i] and at the time to maturity
     * timeDiscretization.getTime(j). Thus, each row corresponds
     * to one underlying value and each column to one time to maturity.
     *
     * @return the price array, with underlying values indexed by rows
     *         and times to maturity indexed by columns
     */
    public double[][] getAllPrices() {
  	
        if (finiteDifferencePrices == null) {
        	//this gets called only once
            generatePrices();
        }

        return finiteDifferencePrices;
    }

    /**
     * Returns the option prices at the specified underlying value for all
     * times to maturity in the time discretization.
     *
     * The element at index i is the price corresponding to time to maturity i
     * in the time discretization for the specified underlying value. 
     * If the specified underlying value is not on the space grid, the closest
     * grid value is used.
     *
     * @param underlyingValue the underlying value at which prices are requested
     * @return the option prices, in the same order as the time grid: the first one
     * is for the smallest time to maturity, the last one for the biggest time to
     * maturity
     */
    public double[] getAllPricesForSpecificUnderlyingValue(double underlyingValue) {
        if (spaceGrid == null) {
        	generateSpaceGrid();
        }

        /*
         * If the specified underlying value is not on the space grid, the closest grid
         * value less is used.
         */
        int underlyingValueIndex = UsefulMethodsForArrays.indexOfNearest(spaceGrid.getGrid(), underlyingValue);

        if (finiteDifferencePrices == null) {
            generatePrices();
        }
        
        /*
         * Remember that prices at a specific underlying value are stored in the matrix row
         * identified by the underlying value index
         */
        double[] pricesAtUnderlyingValue = finiteDifferencePrices[underlyingValueIndex];
        return pricesAtUnderlyingValue;
    }
    
    
    /**
     * Returns the option prices at the specified time to maturity for all underlying
     * values in the space grid.
     *
     * The element at index i is the price corresponding to the underlying
     * value at index i in the space grid. If the specified time to maturity is not
     * on the time grid, the closest grid time less than or equal to it is used.
     *
     * @param timeToMaturity the time to maturity at which prices are requested
     * @return the option prices, in the same order as the space grid
     */
    public double[] getAllPricesAtSpecificTimeToMaturity(double timeToMaturity) {
        if (timeDiscretization == null) {
            generateTimeDiscretization();
        }

        /*
         * If the specified time to maturity is not on the time grid, the closest grid
         * time less than or equal to it is used.
         */
        int timeIndex = timeDiscretization.getTimeIndexNearestLessOrEqual(timeToMaturity);

        if (finiteDifferencePrices == null) {
            generatePrices();
        }
        
        /*
         * Remember that prices at a specific time to maturity are stored in the matrix column
         * identified by the time index
         */
        double[] pricesAtTimeIndex = UsefulMethodsForArrays.getColumn(finiteDifferencePrices, timeIndex);
        return pricesAtTimeIndex;
    }
    
    
    

    /**
     * Prints finite-difference prices for every combination of the requested
     * underlying values and times to maturity.
     *
     * Each underlying value is mapped to its nearest spatial grid point.
     * Each time is mapped to the greatest temporal grid point less than or
     * equal to it. The printed labels are the requested values.
     *
     * @param timesToMaturity the times to maturity at which prices are requested
     * @param underlyingValues the underlying values at which prices are requested
     */
    public void printPricesAtSpecificTimesToMaturityAndUnderlyingValues(double[] timesToMaturity, double[] underlyingValues) {
        // We get the grids and prices only if they have not already been stored
        if (timeDiscretization == null) {
            generateTimeDiscretization();
        }
        if (spaceGrid == null) {
            generateSpaceGrid();
        }

        if (finiteDifferencePrices == null) {
            generatePrices();
        }

        double[] spaceGridAsDoubleArray = spaceGrid.getGrid();

        
        for (double underlyingValue : underlyingValues) {
        	/*
             * For each requested underlying value, we find the nearest value in the
             * space grid. Its index identifies the row containing the prices for
             * that grid value.
             */
            int correspondingSpaceIndex =
                    UsefulMethodsForArrays.indexOfNearest(
                            spaceGridAsDoubleArray, underlyingValue);
            
            for (double time : timesToMaturity) {
            	/*
                 * For each requested time to maturity, we select the latest grid time that does
                 * not exceed it. Its index identifies the column to read.
                 */
                int correspondingTimeIndex =
                        timeDiscretization.getTimeIndexNearestLessOrEqual(time);

                double price =
                        finiteDifferencePrices[correspondingSpaceIndex][correspondingTimeIndex];
                
                /*
                 * This is a method of this class to print a row with time to maturity, underlying value
                 * and corresponding price, with a "nice" number of decimal digits
                 */
                printResult(time, underlyingValue, price);
            }
        }
    }

    /**
     * Returns finite-difference prices for every combination of the requested
     * underlying values and times to maturity.
     *
     * Each underlying value is mapped to its nearest spatial grid point.
     * Each time to maturity is mapped to the greatest temporal grid point less than or
     * equal to it. 
     *
     * @param times the times to maturity at which prices are requested
     * @param underlyingValues the underlying values at which prices are requested
     * @return a matrix whose entry [i][j] is the option price for underlyingValues[i]
     * at timesToMaturity[j], using the corresponding points selected from the space and time grids
     */
    public double[][] getPricesAtSpecificTimesToMaturityAndUnderlyingValues(double[] timesToMaturity, double[] underlyingValues) {
    	// We get the grids and prices only if they have not already been stored
        if (timeDiscretization == null) {
            generateTimeDiscretization();
        }
        if (spaceGrid == null) {
            generateSpaceGrid();
        }

        if (finiteDifferencePrices == null) {
            generatePrices();
        }

        //needed to initialize the array we have to return
        int numberOfSelectedUnderlyingValues = underlyingValues.length;
        int numberOfSelectedTimes = timesToMaturity.length;

        //we initialize of the array
        double[][] selectedPrices = new double[numberOfSelectedUnderlyingValues][numberOfSelectedTimes];

        double[] spaceGridAsDoubleArray = spaceGrid.getGrid();

        // Each outer-loop iteration fills the row for one underlying value
        for (int underlyingValueIndex = 0; underlyingValueIndex<numberOfSelectedUnderlyingValues; underlyingValueIndex++) {
            
        	/*
             * For each requested underlying value, we find the nearest value in the
             * space grid. Its index identifies the row containing the prices for
             * that grid value.
             */
        	int correspondingSpaceIndex =
                    UsefulMethodsForArrays.indexOfNearest
                    (spaceGridAsDoubleArray, underlyingValues[underlyingValueIndex]);
            for (int selectedTimeIndex = 0; selectedTimeIndex < numberOfSelectedTimes; selectedTimeIndex++ ) {
            	/*
                 * For each requested time to maturity, we select the latest grid time that does
                 * not exceed it. Its index identifies the column to read.
                 */
            	int correspondingTimeIndex =
                		timeDiscretization.getTimeIndexNearestLessOrEqual(timesToMaturity[selectedTimeIndex]);
                double price = finiteDifferencePrices[correspondingSpaceIndex][correspondingTimeIndex];
                selectedPrices[underlyingValueIndex][selectedTimeIndex]=price;
            }
        }
        return selectedPrices;
    }

 

    /**
     * Displays the price curve over a selected spatial interval at each
     * requested time to maturity, updating the same plot between times.
     *
     * Only points of the existing spatial grid within the inclusive
     * interval are plotted. At least two such points are required. The title
     * reports the grid time actually used for each curve.
     *
     * @param times the requested times to maturity
     * @param minUnderlying the lower end of the displayed spatial interval
     * @param maxUnderlying the upper end of the displayed spatial interval
     */
    public void plotPricesAtSpecificTimesToMaturity(
            double[] timesToMaturity, double minUnderlying, double maxUnderlying)
            throws InterruptedException {


    	// We get the grids and prices only if they have not already been stored
        if (timeDiscretization == null) {
            generateTimeDiscretization();
        }
        if (spaceGrid == null) {
            generateSpaceGrid();
        }
        if (finiteDifferencePrices == null) {
            generatePrices();
        }

        double[] grid = spaceGrid.getGrid();

        /*
         *  We count the existing grid points within the requested interval, to check
         *  if they are at least two and then to initialize the array of points to be
         *  plotted
         */
        int count = 0;
        for (double underlying : grid) {
            if (underlying >= minUnderlying && underlying <= maxUnderlying) {
                count++;
            }
        }
        if (count < 2) {
            throw new IllegalArgumentException(
                    "The interval must contain at least two space-grid points.");
        }

        double[] plottedUnderlying = new double[count];
        int[] spaceIndices = new int[count];

        /*
         * We copy the grid values within the chosen interval into plottedUnderlying.
         * These will be the x-coordinates of the curve.
         *
         * For each copied value, we store its position in the original space grid
         * in spaceIndices. That position identifies the row of finiteDifferencePrices
         * containing its prices at different times to maturity.
         *
         * Here, position counts how many selected grid values have been copied so far.
         */
        int position = 0;
        //This can be made more efficient. We will discuss this.
        for (int i = 0; i < grid.length; i++) {
        	//if grid[i] in inside the interval..
            if (grid[i] >= minUnderlying && grid[i] <= maxUnderlying) {
                plottedUnderlying[position] = grid[i];
                spaceIndices[position] = i;
                position++;
            }
        }
        

        Plot2D plot = null;

        for (double requestedTime : timesToMaturity) {
            // We select the greatest grid time not exceeding the requested time to maturity
            int timeIndex =
                    timeDiscretization.getTimeIndexNearestLessOrEqual(requestedTime);

            // We get one spatial price curve from the stored price array
            double[] plottedPrices = new double[count];
            for (int j = 0; j < count; j++) {
                plottedPrices[j] =
                        finiteDifferencePrices[spaceIndices[j]][timeIndex];
            }

            Plotable2D curve = PlotablePoints2D.of(
                    "Option price", plottedUnderlying, plottedPrices, null);

            /* 
             * We create the window for the first time. We will then replace its curve for
             * every subsequent time instead of opening another window. 
             * 
             */
            if (plot == null) {
                plot = new Plot2D(java.util.Collections.singletonList(curve));
                plot.setXAxisLabel("Underlying value");
                plot.setYAxisLabel("Option price");
                plot.setXRange(plottedUnderlying[0],
                        plottedUnderlying[count - 1]);
                plot.setIsLegendVisible(false);
                plot.show();
            } else {
                plot.update(java.util.Collections.singletonList(curve));
            }

            // We label the curve with the grid time actually used
            plot.setTitle(String.format(java.util.Locale.US,
                    "Option prices at time t = %.2f",
                    timeDiscretization.getTime(timeIndex)));

            // We leave each curve visible before moving to the next time
            Thread.sleep(PLOT_DELAY_MILLIS);
        }
    }

    /*
     * Internal method used to print one labelled price using the decimal precision specified by
     * the class constants.
     */
    private static void printResult(double time, double underlying, double price) {
        String format = "Time to maturity: %." + TIME_AND_UNDERLYING_DECIMALS
                + "f | Underlying: %." + TIME_AND_UNDERLYING_DECIMALS
                + "f | Price: %." + PRICE_DECIMALS + "f%n";

        System.out.printf(java.util.Locale.US, format, time, underlying, price);
    }
}