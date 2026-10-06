package it.univr.montecarlo.numbersgeneration;

/**
 * In this class we make experiments regarding overflows and underflows in Java, since this is
 * connected to the results we get in LinearCongruentialGenerator if we don't do any correction.   
 */
public class OverflowExperiments {

	public static void main(String[] args) {
		
		
		//here we see overflows and underflows with integers
		
		System.out.println("OVERFLOWS AND UNDERFLOWS WITH INTEGER:");
		System.out.println();
		System.out.println();
		//first overflows
		int maximumIntegerValue = Integer.MAX_VALUE;
		System.out.println("The maximum int value is " + maximumIntegerValue);//2^31-1
		System.out.println();
		for (int valueOfOverflow = 1; valueOfOverflow<=4; valueOfOverflow++) {
			//we go in the negative part of the wheel
			int maximumIntegerValuePlusOverflow = maximumIntegerValue+valueOfOverflow;
			System.out.println("The maximum int value plus " + valueOfOverflow + " is " + maximumIntegerValuePlusOverflow);
		}
		
		System.out.println();
		
		int minimumIntegerValue = Integer.MIN_VALUE;
		System.out.println("The minimum int value is " + minimumIntegerValue);//-2^31
		System.out.println();
		for (int valueOfUnderflow = 1; valueOfUnderflow<=4; valueOfUnderflow++) {
			//we go in the positive part of the wheel
			int minimumIntegerValueMinusUnderflow = minimumIntegerValue-valueOfUnderflow;
			System.out.println("The minimum int value minus  " + valueOfUnderflow + " is " + minimumIntegerValueMinusUnderflow);
		}
		
		System.out.println();
		System.out.println("-------------------------------------");
		System.out.println();
		
		//now overflows and underflows with long
		
		System.out.println("OVERFLOWS AND UNDERFLOWS WITH LONG:");
		System.out.println();
		System.out.println();
		long maximumLongValue = Long.MAX_VALUE;
		System.out.println("The maximum long value is " + maximumLongValue);//2^63-1
		long maximumLongValuePlusOverflow = maximumLongValue+1;
		System.out.println("The maximum long value plus " + 1 + " is " + maximumLongValuePlusOverflow);
		
		System.out.println();

		long minimumLongValue = Long.MIN_VALUE;
		System.out.println("The minimum long value is " + minimumLongValue);//-2^63
		long minimumLongValueMinusUnderflow = minimumLongValue-1;
		System.out.println("The minimum long value minus  " + 1 + " is " + minimumLongValueMinusUnderflow);
	}

}
