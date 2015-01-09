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
	public static int numRuns = 1; // number of replications for current setting
	public static int periods = 100; // number of runs per replication
	public static Landscape landscape;
	public static int N = 8;
	private static String outfilename; //= ""; // "results/joint_n16k0_0.txt"
	private static String influenceMatrixFile = "conf/n16k0.txt";
	// public static int numOrgs = 100; // number of organizations to create within replication
	// public static int busOverlap = 0; // number of overlapping elements from IS that business knows 
	public static int isOverlap = 0; // number of overlapping elements from Bus that IS knows
	public static String orgType = "agile"; // sequential | iterative | agile | joint
	public static int numUnits = 2;
	public static String[] unitNames;
	public static int[] domainDistributionsCounts;
	// public static int[] kdists;
	public static boolean authority = false; // whether Bus can change IS or IS can change Bus; no need for now
	public static int numAlternatives; // processing power; no need for now; 
	public static double preferentialWeightage; 

	public static String reportLevel = "summary"; // reportLevel = {summary, details}
	public static boolean debugToFile = false;
	public static boolean replicate = true; // if replicate true; then runID is sequential; otherwise SystemMillis
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
				periods = Integer.parseInt(p.getProperty("periods")); // number of runs 
				numRuns = Integer.parseInt(p.getProperty("runs"));
				outfilename = p.getProperty("outfile");
				if (outfilename == null) {
					out = new PrintWriter(System.out);
				} else {
					out = new PrintWriter(new FileOutputStream(outfilename, true), true);
				}
				debugfile = p.getProperty("debugfile");
				influenceMatrixFile = p.getProperty("influenceMatrix");
				numOrgs = Integer.parseInt(p.getProperty("numOrgs"));
//				overlap = Integer.parseInt(p.getProperty("overlap"));
				busOverlap = Integer.parseInt(p.getProperty("busOverlap"));
				isOverlap = Integer.parseInt(p.getProperty("isOverlap"));
				N = Integer.parseInt(p.getProperty("N"));
				orgType = p.getProperty("orgType");
				numUnits = Integer.parseInt(p.getProperty("numUnits"));
				unitNames = p.getProperty("unitNames").split(","); // array

				// [added 3/24/12]

				// e.g,. domainDistribution=4,8,4 for aaaabbbbbbbbcccc or 10,6 for aaaaaaaaaabbbbbb
				domainDistributionsCounts = new int[p.getProperty("domainDistribution").split(",").length];
				for (int i =0; i < p.getProperty("domainDistribution").split(",").length; i++) {
					domainDistributionCounts[i] = Integer.parseInt(p.getProperty("domainDistribution").split(",")[i]);
				}

				// String[] kdistributions = p.getProperty("kdistribution").split(",");
				// kdists = new int[kdistributions.length];
				// for (int i = 0; i < kdistributions.length; i++) {
				// 	kdists[i] = Integer.parseInt(kdistributions[i]); 
				// }

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

		}  else { // end if confFile
			setDefaults();
		}
		
		try {
			// create output printwriter
			out = new PrintWriter(new FileOutputStream(outfilename, true), true);
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}
		Debug.println("Globals.loadGlobals: configFile loaded");

	}
	
	private static void setDefaults() {
		numRuns = 1; // number of replications for current setting
		periods = 100; // number of runs per replication
		N = 16;
		outfilename = "results/default_testing.txt"; 
		influenceMatrixFile = "conf/n16k7.txt";
		numOrgs = 1; // number of organizations to create within replication
		busOverlap = 0; // number of overlapping elements from IS that business knows 
		isOverlap = 0; // number of overlapping elements from Bus that IS knows
		orgType = "agile"; // sequential | iterative | agile | joint
		numUnits = 2;
		unitNames[0] = "business"; unitNames[1] = "IS";
		domainDistributionsCounts[0] = 8; domainDistributionsCounts[1] = 8;
		authority = false; // whether Bus can change IS or IS can change Bus; no need for now
		numAlternatives = 1; // processing power; no need for now; 
		reportLevel = "summary"; // reportLevel = {summary, details}
		debugToFile = true;
		replicate = true; // if replicate true; then runID is sequential; otherwise SystemMillis
		localAssessment = "ac2010"; // for almirall & casadesus-masanell 2010 or "gl2000" for gavetti and levinthal
	}

	public static void createLandscape(int id) {
		landscape  = new Landscape(id, new InfluenceMatrix(influenceMatrixFile));
		Debug.println("Glboals.createLandscape: landscape created at " + id + " with maxFitness " + landscape.getMaxFitness());

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

	/** Accessors **/
	public static int getN() {
		return N;
	}


}
