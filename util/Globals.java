package util;

import util.MersenneTwisterFast;
import util.Debug;
import java.util.Properties;
import java.io.File;
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
	public static int numOrgs = 100; // number of organizations to create within replication
//	public static int busOverlap = 0; // number of overlapping elements from IS that business knows 
//	public static int isOverlap = 0; // number of overlapping elements from Bus that IS knows
	public static String orgType = "agile"; // sequential | iterative | agile | joint
	public static int numUnits = 2;
	public static String[] unitNames;
	public static int[] domainDistributionsCounts;
	public static String localKnowledgeIndices; // IMPLEMENT IN loadGlobals()
	// public static int[] kdists;
	// public static boolean authority = false; // whether Bus can change IS or IS can change Bus; no need for now
	public static int numAlternatives = 1; // processing power; no need for now; 
	public static double preferentialWeightage; 

	public static String reportLevel = "summary"; // reportLevel = {summary, details}
	public static boolean debugToFile = false;
	public static boolean replicate = true; // if replicate true; then runID is sequential; otherwise SystemMillis
	public static int startLandscapeID;
	public static String localAssessment = "ac2010"; // for almirall & casadesus-masanell 2010 or "gl2000" for gavetti and levinthal
	// @todo: check what is the difference in implementation between ac2010 and gl2000
	public static String neighborSelectionApproach = "random"; // "random" vs. "myknowledge" vs. "othersknowledge" vs. "cross"
									// random -> pick random neighbor
									// myknowledge -> pick based on my shared knowledge of other domains 
									// othersknowledge -> pick based on others shared knowledge of my domain
									// cross -> ?????

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
//				out = (outfilename == null) ? new PrintWriter(System.out) : new PrintWriter(new FileOutputStream(outfilename, true), true);
				if (outfilename == null) {
					out = new PrintWriter(System.out);
				} else {
					new File("results").mkdirs();
				 	out = new PrintWriter(new FileOutputStream("results/" + outfilename, true), true);
				}
//				debugfile = p.getProperty("debugfile");
				influenceMatrixFile = p.getProperty("influenceMatrix");
				numOrgs = Integer.parseInt(p.getProperty("numOrgs"));
//				overlap = Integer.parseInt(p.getProperty("overlap"));
//				busOverlap = Integer.parseInt(p.getProperty("busOverlap"));
//				isOverlap = Integer.parseInt(p.getProperty("isOverlap"));
				N = Integer.parseInt(p.getProperty("N"));
				orgType = (p.getProperty("orgType") == null) ? "agile" : p.getProperty("orgType");
				numUnits = Integer.parseInt(p.getProperty("numUnits"));
				if (p.getProperty("unitNames") == null) {
					for (int i = 0; i < numUnits; i++) {
						unitNames[i] = "unit" + i;
					}
				} else {
					unitNames = p.getProperty("unitNames").split(","); // array
				}
	
				neighborSelectionApproach = p.getProperty("search");

				// e.g,. domainDistribution=4,8,4 for aaaabbbbbbbbcccc or 10,6 for aaaaaaaaaabbbbbb
				domainDistributionsCounts = new int[p.getProperty("domainDistribution").split(",").length];
				for (int i =0; i < p.getProperty("domainDistribution").split(",").length; i++) {
					domainDistributionsCounts[i] = Integer.parseInt(p.getProperty("domainDistribution").split(",")[i]);
				}
				//localKnowledgeIndices = "1,1,0,0,0,0;0,0,1,1,0,0;0,0,0,0,1,1";; NO CHECKING HERE
				localKnowledgeIndices = p.getProperty("knowledgeIndices");
				String[] kIndices = localKnowledgeIndices.split(";");
				for (int i = 0; i < kIndices.length; i++) {
					String[] unitIndices = kIndices[i].split(",");
					if (unitIndices.length != Globals.N) {
						System.err.println("knowledge index for unit " + i + " (" + unitIndices.length + ") does not equal " + Globals.N + "[" + kIndices[i] + "]");
						System.exit(0);
					}
				}
				// neighborSelection = {random, myknowledge, othersknowledge, cross}
				neighborSelectionApproach = (p.getProperty("neighborSelection") == null) ? "random" : p.getProperty("neighborSelection");

				// String[] kdistributions = p.getProperty("kdistribution").split(",");
				// kdists = new int[kdistributions.length];
				// for (int i = 0; i < kdistributions.length; i++) {
				// 	kdists[i] = Integer.parseInt(kdistributions[i]); 
				// }
				startLandscapeID = (p.getProperty("startLandscapeID") == null) ? 0 : Integer.parseInt(p.getProperty("startLandscapeID"));

				// [end add]
				reportLevel = (p.getProperty("reportLevel") == null) ? "summary" : "details";
				// reportLevel = p.getProperty("reportLevel");
				// if (p.getProperty("authority").equals("true") || p.getProperty("authority").equals("1")) { 
				// 	authority = true; 
				// } else { 
				// 	authority = false; 
				// }

				if (p.getProperty("debug").equals("true") || p.getProperty("debug").equals("1")) { 
					Debug.setDebug(true, p.getProperty("debugToFile").equals("true") || p.getProperty("debugToFile").equals("1")); 
					Debug.println(getConfigurations());
				}

				localAssessment = (p.getProperty("fitnessCalc") == null) ? "gl2000" : "ac2010"; // not needed here (default = gavetti & levinthal 2000)
				
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			} // END try..catch

		}  else { // end if confFile
			setDefaults();
		}
		
//		try {
//			// create output printwriter
//			out = new PrintWriter(new FileOutputStream(outfilename, true), true);
//		} catch (IOException io) {
//			System.err.println(io.getMessage());
//			io.printStackTrace();
//		}
		Debug.println("Globals.loadGlobals: configFile loaded");

	}
	
	private static void setDefaults() {
		numRuns = 1; // number of replications for current setting
		periods = 50; // number of runs per replication
		N = 8;
		outfilename = "default_testing.txt"; 
		try {
			new File("results").mkdirs();
		 	out = new PrintWriter(new FileOutputStream("results/" + outfilename, true), true);
		} catch  (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}
		preferentialWeightage = 2d;
		influenceMatrixFile = "inf/n8k1.txt";
		numOrgs = 1; // number of organizations to create within replication
//		busOverlap = 0; // number of overlapping elements from IS that business knows 
//		isOverlap = 0; // number of overlapping elements from Bus that IS knows
		orgType = "agile"; // sequential | iterative | agile | joint
		numUnits = 2;
		unitNames = new String[]{"business", "IS"};
//		unitNames[0] = "business"; unitNames[1] = "IS";
		domainDistributionsCounts = new int[]{4, 4};
//		domainDistributionsCounts[0] = 4; domainDistributionsCounts[1] = 4;
		localKnowledgeIndices = "1,1,1,1,1,1,1,0;0,0,1,0,1,1,1,1";
		neighborSelectionApproach = "random";
		startLandscapeID = 0;
//		authority = false; // whether Bus can change IS or IS can change Bus; no need for now
		numAlternatives = 1; // processing power; no need for now; 
		reportLevel = "summary"; // reportLevel = {summary, details}
//		debugToFile = true;
		Debug.setDebug(true, true);
		replicate = true; // if replicate true; then runID is sequential; otherwise SystemMillis
		localAssessment = "gl2000"; // for almirall & casadesus-masanell 2010 or "gl2000" for gavetti and levinthal
	}

	public static void createLandscape(int id) {
		landscape  = new Landscape(id, new InfluenceMatrix(influenceMatrixFile));
		Debug.println("Globals.createLandscape: landscape created at " + id + " with maxFitness " + landscape.getMaxFitness());

	}
	
	public static void setRandomNumbers(int intRunID) {
		long runID;
		if (replicate) { runID = (long)intRunID;
		} else { runID = System.currentTimeMillis(); }
		rand = new MersenneTwisterFast(runID);
//		Debug.println("Globals.setRandomNumbers: random number seed set to: " + runID);
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
		retString += "number of sub-organizations: " + numUnits + "\n";
//		retString += "overlap (bus/IS): " + busOverlap + "/" + isOverlap + "\n";
		retString += "outfile: " + outfilename + "\n";
		retString += "report level: " + reportLevel + "\n";
		retString += "debug: " + Debug.isDebugOn() + "\n";
		retString += "debug out: " + Debug.getOutFile() + "\n";
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
