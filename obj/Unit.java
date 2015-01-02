package obj;

import java.util.Collections;
import java.util.Vector;
import util.Globals;

public class Unit {
	private int index;
	private String unitName;
//	private int localKnowledgeIndex[] = new int[Globals.N / 2]; // [commented 3/24/12]
	/**
	 * domain  			- what elements are within the purview of a unit
	 * fullKnowledge 	- what the unit knows; (includes overlapping knowledge of other domains) --> ownKnowledge OR(+) othersKnowledge
	 * ownKnowledge		- what the unit knows in own domain; --> domain AND fullKnowledge
	 * othersKnowledge 	- what the unit knows in others' domains; 
	 **/
	private boolean domain[];
	private boolean fullKnowledge[];
	private boolean ownKnowledge[];
	private boolean othersKnowledge[];

	// private boolean control[] = new boolean[Globals.N]; // NO LONGER NEEDED; authority is assumed to be false; so for now this is the same as domain[]
//	private Location globalLocation;
//	private Location localLoc; 
	//private int knowledgeSize = 0;

	// private int overlapIndex[] = new int[Globals.busOverlap];
	private int localKnowledgeIndex[]; 
	private int knowledgeOverlapIndex[];
	private Location moveTo;

//	private boolean move; 
	private Vector<Location> neighbors;


	/** constructor **/
	/** @params
	 * idx 					- index unit
	 * name 				- name of unit (IS, business, or just unit1 etc.) mainly for reporting purposes
	 * domainDistributionCnts - distribution of number of domains e.g., [4,8,4] where N = 16 with 3 units
	 * knowledgeIdxs 		- the String representation of ALL units' knowledge.  ALL other units' knowledge is needed so that we 
	 *						  can assign overlapping knowledge to others' known elements
	 *						- e.g., knoweldgeIdx = "1,1,0,0,0,0;0,0,1,1,0,0;0,0,0,0,1,1" -> [[1,1,0,0,0,0],[0,0,1,1,0,0],[0,0,0,0,1,1]]
	 * knowledgeOverlapSize - the number of other units' knowledge elements this unit is knowledgeable about -- extent of shared 
	 *						  domain knowledge; this is used to pick 
	 * called by Organization: units[i] = new Unit(i, Globals.unitNames[i], Globals.domainDistributionCounts, Globals.localKnowledgeIndices, Globals.knowledgeOverlapIndex[i]);
	 **/
	//public Unit(int idx, String name, int[] domainDistributionCnts, String knowledgeIdxs, int knowledgeOverlapSize) {
	public Unit(int idx, String name, int[] domainDistributionCnts, String knowledgeIdxs) {
		// set unit index and name
		index = idx;
		unitName = name;

		// set unit's knowledge domain (control)
		setDomain(domainDistributionCnts); // set own knowledge domain 
		setKnowledges(knowledgeIdxs);

		//
		/** @todofor now; not sure if Unit will be responsible for search or Organization
		// if (Globals.debug) { System.out.println("overlap set for infoSys"); }
		// setLocation(globalLoc);
		// if (Globals.debug) { System.out.println("set location for infoSys"); }
		// resetSearchHistory();
		// if (Globals.debug) { System.out.println("reset search history for infoSys"); }
		*/
		
	}

	private void setDomain(int[] domainDistributionCnts) {
		// set control domain; e.g., domainDistributionCnts = [4,8,4] 
		// => e.g., domain[] for unit1 = [t,t,t,t,f,f,f,f,f,f,f,f,f,f,f,f] for unit2 = [f,f,f,f,t,t,t,t,t,t,t,t,f,f,f,f]
		domain = new boolean[Globals.N]; // initialized to all false 
		int tmpStart = 0;
		for (int i = 0; i < domainDistributionCnts.length; i++) {
			if (index == i) {
				for (int j = tmpStart; j < tmpStart + domainDistributionCnts[i]; j++) {
					domain[j] = true;
				}
			} else {
				tmpStart += domainDistributionCnts[i];
			}
		}
	}

	private void setKnowledges(String knowledgeIdxs) {
		fullKnowledge = new boolean[Globals.N]; // initialized to all false 
		ownKnowledge = new boolean[Globals.N]; // initialized to all false 
		othersKnowledge = new boolean[Globals.N]; // initialized to all false 

		// 1.  set fullKnowledge[] -- what the unit knows including overlapping knowledge of others' domains
		for (int i = 0; i < knowledgeIdxs.split(";").length; i++) {
			if (index == i) {
				for (int j = 0; j < knowledgeIdxs.split(";")[i].split(",").length; j++) {
					if (knowledgeIdxs.split(";")[i].split(",")[j].equals("1")) {
						fullKnowledge[j] = true;
					}
				}
			}
		}
		// 2. distinguish between own and others' knowledge
		for (int i = 0; i < fullKnowledge.length; i++) {
			if (fullKnoweldge[i]) { // if unit "knows"
				if (domain[i]) { // if within unit's own domain 
					ownKnowledge[i] = true;
				} else { // if outside of unit's own domain 
					othersKnowledge[i] = true;
				}
			}
		}
	}

	// get own knowledge size -- how many elements the unit knows within own domain
	public int getOwnKnowledgeSize() {
		int retSize = 0; 
		for (int i = 0; i < ownKnowledge.length; i++) {
			if (ownKnowledge[i]) { retSize++; }
		}
		return retSize;
	}

	// get overlapping knowledge size -- how many elements the unit knows outside own domain
	public int getOverlappingKnowedgeSize() {
		int retSize = 0; 
		for (int i = 0; i < othersKnowledge.length; i++) {
			if (othersKnowledge[i]) { retSize++; }
		}
		return retSize;
	}

	// get full knowledge size -- how many elements the unit knows altogether
	public int getFullKnowledgeSize() {
		int retSize = 0; 
		for (int i = 0; i < fullKnowledge.length; i++) {
			if (fullKnowledge[i]) { retSize++; }
		}
		return retSize;
	}
	
}