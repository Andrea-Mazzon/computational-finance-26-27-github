package it.univr.usefulmethodsforarrays;

import java.util.Arrays;

public class UsefulMethodsForArrays {


	/**
	 * It returns the average of the elements of an array
	 * 
	 * @param array
	 * @return the average of the elements of the array
	 */
	public static double getAverage(double array[]) {
		double sum = 0.0;
		for(double value : array) {
			sum += value;
		}
		return sum/array.length;
	}

	/**
	 * It returns the variance of the elements of an array
	 * 
	 * @param array
	 * @return the variance of the elements of the array
	 */
	public static double getVariance(double[] values) {
		final double mean = getAverage(values);
		double sumOfSquaredDeviations = 0.0;

		for (double value : values) {
			final double deviation = value - mean;
			sumOfSquaredDeviations += deviation * deviation;
		}

		return sumOfSquaredDeviations / (values.length - 1);
	}

	public static double[] getColumn(double[][] matrix, int columnIndex) {
		double[] column = new double[matrix.length];

		for (int i = 0; i < matrix.length; i++) {
			column[i] = matrix[i][columnIndex];
		}

		return column;
	}


	/**
	 * It returns the index of an array which corresponds to the array element closest to
	 * a double x
	 * @param values tha array
	 * @param x the double value  
	 * @return the index of the element of array closest to x
	 */
	public static int indexOfNearest(double[] values, double x) {

		int index = Arrays.binarySearch(values, x);
		if (index >= 0) {
			return index; 
		}

		int right = -index - 1;      
		if (right == 0) {
			return 0;
		}
		if (right == values.length) {
			return values.length - 1;
		}

		int left = right - 1;
		return x - values[left] <= values[right] - x ? left : right;
	}
}