package util;

import util.MersenneTwisterFast;
import util.Debug;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;

import obj.InfluenceMatrix;
import obj.Landscape;

public class Globals {
	/**
	 * simulation parameters: default values
	 */
	public static int N = 8;
//	public static int K = 2; // no need
	public static int periods = 100;
//	private static String outfilename = "results/test.txt";
	private static String outfilename = "results/joint_n16k0_0.txt";
	private static String influenceMatrixFile = "conf/n16k0.txt";
//	public static int numOrgs = 10;
	public static int numOrgs = 100;
//	public static int overlap = 4; // number of overlapping elements
	public static int busOverlap = 0; // number of overlapping elements from IS that business knows 
	public static int isOverlap = 0; // number of overlapping elements from Bus that IS knows
//	public static boolean authority = true; // whether Bus can change IS or IS can change Bus
	public static boolean authority = false; // whether Bus can change IS or IS can change Bus
	public static String orgType = "joint"; // sequential | iterative | agile | joint
	public static int numSubOrgs = 2;
	// [added 3/24/12]
	public static int[] kdists;
	public static int numAlternatives;
	public static Landscape landscape;
//	public static String reportLevel = "details";
	public static String reportLevel = "summary";
//	public static boolean debug = false;
	public static boolean debugToFile = false;
	public static boolean replicate = true; 
//	private static long seed = 1261505528597l;
	public static double landscapeMax;
	public static int numRuns = 1;
	public static int startLandscapeID;
	public static String localAssessment = "ac2010"; // for almirall & casadesus-masanell 2010 or "gl2000" for gavetti and levinthal
	
	/**
	 * utils
	 */
	public static long runID = System.currentTimeMillis(); // need?
//	private static long runID = 1261505528597l;
	public static PrintWriter out;
	public static MersenneTwisterFast rand = new MersenneTwisterFast(runID);
//	public static MersenneTwisterFast nkrnd = new MersenneTwisterFast(seed);
	public static Random random = new Random();

	public static void loadGlobals(String configFile) {
		if (!configFile.equals("")) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(configFile));
				// simulation parameters
//				seed = Long.parseLong(p.getProperty("seed"));
				periods = Integer.parseInt(p.getProperty("periods"));
				numRuns = Integer.parseInt(p.getProperty("runs"));
				outfilename = p.getProperty("outfile");
				debugfile = p.getProperty("debugfile");
				influenceMatrixFile = p.getProperty("influenceMatrix");
				numOrgs = Integer.parseInt(p.getProperty("numOrgs"));
//				overlap = Integer.parseInt(p.getProperty("overlap"));
				busOverlap = Integer.parseInt(p.getProperty("busOverlap"));
				isOverlap = Integer.parseInt(p.getProperty("isOverlap"));
				N = Integer.parseInt(p.getProperty("N"));
				orgType = p.getProperty("orgType");
				numSubOrgs = Integer.parseInt(p.getProperty("numSubOrgs"));
				// [added 3/24/12]
				String[] kdistributions = p.getProperty("kdistribution").split(",");
				kdists = new int[kdistributions.length];
				for (int i = 0; i < kdistributions.length; i++) {
					kdists[i] = Integer.parseInt(kdistributions[i]);
				}
				String startLandscapeIDStr = p.getProperty("startLandscapeID");
				if (startLandscapeIDStr == null) {
					startLandscapeID = 0;
				} else {
					startLandscapeID = Integer.parseInt(startLandscapeIDStr);
				}
				// [end add]
				reportLevel = p.getProperty("reportLevel");
				if (p.getProperty("authority").equals("true") || p.getProperty("authority").equals("1")) { 
					authority = true; 
				} else { 
					authority = false; 
				}

				if (p.getProperty("debug").equals("true") || p.getProperty("debug").equals("1")) { 
					Debug.setDebug(true, p.getProperty("debugToFile").equals("true") || p.getProperty("debugToFile").equals("1")); 
					// debug = true; 
					// if (p.getProperty("debugToFile").equals("true") || p.getProperty("debugToFile").equals("1")) {
					// 	Debug.setDebugFile();
					// }
					Debug.println(getConfigurations());
				}

				localAssessment = p.getProperty("fitnessCalc");
				
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			} // END try..catch

			// calculate derived values if any
//			numAlternatives = (int)(N / numSubOrgs);

		}  // end if confFile
		
		try {
			// create output printwriter
			out = new PrintWriter(new FileOutputStream(outfilename, true), true);
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}
		Debug.println("Globals.loadGlobals: configFile loaded");

	}
	
	public static void createLandscape(int id) {
		landscape  = new Landscape(id, new InfluenceMatrix(influenceMatrixFile));
		landscapeMax = landscape.getMaxFitness();
		Debug.println("Glboals.createLandscape: landscape created at " + id + " with maxFitness " + landscapeMax);

	}
	
	public static void setRandomNumbers(int intRunID) {
		long runID;
		if (replicate) { runID = (long)intRunID;
		} else { runID = System.currentTimeMillis(); }
		rand = new MersenneTwisterFast(runID);
		Debug.println("Glboals.setRandomNumbers: random number seed set to: " + runID);
	} 
	
	public static String getConfigurations() {
		String retString = "----- CONFIG -----\n";
		retString += "N: " + N + "\n";
	// //	public static int K = 2; // no need
		if (periods == -1) { retString += "periods: until end\n"; } else { retString += "periods: " + periods + "\n"; }
		retString += "influenceMatrix: " + influenceMatrixFile + "\n";
		retString += "outfile: " + outfilename + "\n";
		retString += "rumber of runs: " + numRuns + "\n";
		retString += "number of organizations: " + numOrgs + "\n";
		retString += "number of sub-organizations: " + numSubOrgs + "\n";
		retString += "overlap (bus/IS): " + busOverlap + "/" + isOverlap + "\n";
		retString += "outfile: " + outfilename + "\n";
		retString += "report level: " + reportLevel + "\n";
		retString += "debug: " + Debug.debugOn() + "\n";
		retString += "debug out: " + Debug.output() + "\n";
		retString += "------------------\n";
	// 	public static int[] kdists;
	// 	public static int numAlternatives;
	// 	public static String localAssessment = "ac2010"; // for almirall & casadesus-masanell 2010 or "gl2000" for gavetti and levinthal
		return retString; 
	}

	public static void main(String[] args) {
//		long runID = 1261505528597l;
		long runID = Long.parseLong(args[0]);
		System.out.println(runID);
	}
}
