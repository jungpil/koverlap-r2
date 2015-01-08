package util;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class Debug {
	// debugging set to FALSE by default unless otherwise turned on in Globals
	// if configFile specifies debugToFfile (boolean: true/1), then a printwriter is created
	private static boolean debug = false;
	private static PrintWriter debugPrintWriter;
	private static boolean fileout = false;
	private static String outfilename = "STDOUT"; 
	
	/**** +setDebug 
	 * switches debugger on/off
	 */
	public static void setDebug(boolean debugOn, boolean debugToFile) {
		debug = debugOn;
		try {
			if (debugToFile) {
				outfilename = "debug/debug-" + System.currentTimeMillis().toString() + ".txt";
				debugPrintWriter = new PrintWriter(new FileOutputStream(outfilename, true), true);
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

	public static String isDebugOn() {
		return debug;
	}

	public static String getOutFile() {
		return outfilename;
	}
}
