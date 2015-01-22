package app;

import util.Globals;
import obj.Organization;
import obj.Agile;
import java.util.Vector;
import java.util.Iterator;
import java.io.PrintWriter;
import util.StatCalc;
import util.Debug;

/** 
 * Main application class
 * 
 * @param configfile
 * - load config file
 * - loop iterations
 *   - create new landscape
 *   - create new organizations; report initial setup
 *   - loop organizations
 *     - run; report
 */
public class Simulation {
	private static Vector<Organization> organizations; //= new Vector<Organization>();
	
	/** MAIN APP METHOD */
	public static void main(String args[]) {
		// set simulation configuration
		Globals.loadGlobals(setConfigFile(args));
		Debug.println("configFile loaded");


		for (int j = Globals.startLandscapeID; j < Globals.numRuns; j++) {
			/** Initialize Landscape */
			// set random number seed
			Globals.setRandomNumbers(j);
			// create landscape
			Globals.createLandscape(j);
			Debug.println("Landscape (id = " + j + ") initialized");
			
			/** Create Agents */
			organizations = new Vector<Organization>();			
			for (int i = 0; i < Globals.numOrgs; i++) {
				if (Globals.orgType.equals("agile")) {
					organizations.add(new Agile(i));
				} else {
					System.err.println("Unknown orgType: " + Globals.orgType);
					System.exit(0);
				}
			}
			Debug.println("Organizations (" + Globals.orgType + ") created for landscape " + j);

			/** initial setup -- t = -1 */
			report(-1);

			/** Run one iteration = 1 landscape X NumOrgs organizations */
			runIteration(Globals.periods);

			Debug.println("Finished iteration for landscape (" + j + ")");
			Globals.landscape = null; // just for good measure; destruct landscape object
		}
	}

	/** run simulations for one iteration (replication) */
	private static void runIteration(int periods) {
		// if periods = -1 then run until equilibrium else run (period) number of time ticks
		for (int t = 0; t < periods + 1; t++) {
			Debug.println("Simulation.run()\tperiod:\t" + t);

			for (Organization org : organizations) {
				org.run();
			}
			
			report(t);
			if (allEnded()) break;
		}
	}
	
	/** check arguments 
	 * - if more than 1 arg; INVALID; QUIT
	 * - if 1 arg: arg = configfile
	 * - if 0 arg; use defaults in Globals class
	 */
	private static String setConfigFile(String[] args) {
		String retString = "";
		if (args.length > 1) {
			System.err.println("Need at most one argument (config file).  Try again.");
			System.exit(0);
		} else if (args.length == 0) {
			retString = "";
			
		} else {
			retString = args[0];
		}
		return retString;
	}
	
	/** Reporting methods*/
	private static void report(int period) {
		if (Globals.reportLevel.equals("details")) reportDetails(period);
		if (Globals.reportLevel.equals("summary")) reportSummary(period);
	}

	private static void reportDetails(int period) {
		for (Organization org : organizations) {
			org.printDetails(period);
		}
	}

	private static void reportSummary(int period) {
		// calc average and report average for landscape
		StatCalc stat = new StatCalc();
		int completed = 0;
		for(Organization org : organizations) {
			stat.enter(org.getOrgFitness());
//			stat.enter(org.getFitness());
			if (org.finished()) { completed++; }
		}
		Globals.out.println(Globals.landscape.getLandscapeID() + "\t" + period + "\t" + completed + "\t" + stat.getMean() + "\t" + stat.getStandardDeviation() + "\t" + stat.getMin() + "\t" + stat.getMax());
	}

	/** checks if all agents are done; for early break */
	private static boolean allEnded() {
		boolean retBool = true;
		for (Organization org : organizations) {
			if (!org.finished()) {
				retBool = false; 
				break;
			}
		}
		return retBool;
	}
	
}
