package util;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class Debug {
	// debugging set to FALSE by default unless otherwise turned on in Globals
	// if configFile specifies debugToFfile (boolean: true/1), then a printwriter is created
	private static boolean debug = false;
	private static PrintWriter out;
	private static boolean fileout = false;
	private static String outfilename; 
	
	/**** +setDebug 
	 * switches debugger on/off
	 */
	public static void setDebug(boolean debugOn, boolean debugTofile) {
		debug = debugOn;
		if (debugToFile) {
			Debug.setDebugFile();
		}
	}
	

	/**** -setDebugFile
	 * creates file printwriter for debuggering file
	 */
	private static void setDebugFile() {
		outfilename = "debug-" + System.currentTimeMillis().toString() + ".txt";
		try {
			// create debug printwriter
			out = new PrintWriter(new FileOutputStream(outfilename, true), true);
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}
		fileout = true;
	}

	public static void print(String s) {
		if (debug) { 
			if (fileout) {
				out.print(s);
			} else {
				System.out.print(s); 
			}
		}
	}
	
	public static void println(String s) {
		if (debug) { 
			if (fileout) {
				out.println(s);
			} else {
				System.out.println(s); 
			}
		}
	}

	public static String debugOn() {
		if (debug) {
			return "true";
		} else {
			return "false";
		}
	}

	public static String output() {
		if (fileout) {
			return outfilename;
		} else {
			return "STDOUT";
		}
	}
}
