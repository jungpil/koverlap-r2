package util;

/* 
An object of class StatCalc can be used to compute several simple statistics
for a set of numbers.  Numbers are entered into the dataset using
the enter(double) method.  Methods are provided to return the following
statistics for the set of numbers that have been entered: The number
of items, the sum of the items, the average, the standard deviation,
the maximum, and the minimum.
*/

public class StatCalc {

	private static int count;   // Number of numbers that have been entered.
	private static double sum;  // The sum of all the items that have been entered.
	private static double squareSum;  // The sum of the squares of all the items.
	private static double max = Double.NEGATIVE_INFINITY;  // Largest item seen.
	private static double min = Double.POSITIVE_INFINITY;  // Smallest item seen.
	
	public static void reset() {
		count = 0;
		sum = 0d;
		squareSum = 0d;
		max = Double.NEGATIVE_INFINITY;
		min = Double.POSITIVE_INFINITY;
	}
	
	public static void enter(double num) {
	      // Add the number to the dataset.
	   count++;
	   sum += num;
	   squareSum += num*num;
	   max = (num > max) ? num : max; 
	   min = (num < min) ? num : min;
	}
	
	public static int getCount() {   
	      // Return number of items that have been entered.
	   return count;
	}
	
	public static double getSum() {
	      // Return the sum of all the items that have been entered.
	   return sum;
	}
	
	public static double getMean() {
	      // Return average of all the items that have been entered.
	      // Value is Double.NaN if count == 0.
	   return sum / count;  
	}
	
	public static double getStandardDeviation() {  
	     // Return standard deviation of all the items that have been entered.
	     // Value will be Double.NaN if count == 0.
	   double mean = getMean();
	   return Math.sqrt( squareSum/count - mean*mean );
	}
	
	public static double getMin() {
	     // Return the smallest item that has been entered.
	     // Value will be infinity if no items have been entered.
	   return min;
	}
	
	public static double getMax() {
	     // Return the largest item that has been entered.
	     // Value will be -infinity if no items have been entered.
	   return max;
	}
	
}  // end class StatCalc


