package util;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

public class Debug {
	// debugging set to FALSE by default unless otherwise turned on in Globals
	// if configFile specifies debugToFfile (boolean: true/1), then a printwriter is created
	private static boolean debug = false;
	private static PrintWriter debugPrintWriter;
	private static String outfilename = "STDOUT"; 
	
	/**** +setDebug 
	 * switches debugger on/off
	 */
	public static void setDebug(boolean debugOn, boolean debugToFile) {
		debug = debugOn;
		try {
			if (debugToFile) {
				new File("debug").mkdirs();
				// @TODO change back after testing
//				outfilename = "debug-" + System.currentTimeMillis() + ".txt"; 
				outfilename = "debug.txt";
				debugPrintWriter = new PrintWriter(new FileOutputStream("debug/" + outfilename, true), true);
			} else {
				debugPrintWriter = new PrintWriter(System.err);
			}
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}
	}
	
	public static void print(String s) {
		if (debug) debugPrintWriter.print(s);
	}
	
	public static void println(String s) {
		if (debug) debugPrintWriter.println(s);
	}

	public static boolean isDebugOn() {
		return debug;
	}

	public static String getOutFile() {
		return outfilename;
	}

	public static String arrayToString(boolean[] array) {
		String retString = "["; 
		for (int i = 0; i < array.length - 1; i++) {
			retString += (array[i]) ? "t" : "f"; 
		}
		retString += (array[array.length - 1]) ? "t]" : "f]";
		return retString;
	} 

	public static String arrayToString(int[] array) {
		String retString = "["; 
		for (int i = 0; i < array.length - 1; i++) retString += array[i] + ", ";
		retString += array[array.length - 1] + "]";
		return retString;
	} 

	public static String arrayToString(double[] array) {
		String retString = "["; 
		for (int i = 0; i < array.length - 1; i++) retString += array[i] + ", ";
		retString += array[array.length - 1] + "]";
		return retString;
	} 

	public static String arrayToString(String[] array) {
		String retString = "["; 
//		for (int i = 0; i < array.length - 1; i++) retString += array[i] + ", ";
		for (int i = 0; i < array.length - 1; i++) retString += array[i];
		retString += array[array.length - 1] + "]";
		return retString;
	} 

}
