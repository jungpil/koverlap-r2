package obj;

import java.util.Collections;
import java.util.Vector;
import util.Globals;

public class Unit {
	private index;
	private String unitName;
//	private int localKnowledgeIndex[] = new int[Globals.N / 2]; // [commented 3/24/12]
	/**
	 * domain  		- what elements are within the purview of a unit
	 * knowledge 	- what the unit knows; (includes overlapping knowledge of other domains)
	 **/
	private boolean domain[] = new boolean[Globals.N]; // initialized to all false 
	private boolean knowledge[] = new boolean[Globals.N]; // initialized to all false 
	// private boolean control[] = new boolean[Globals.N]; // NO LONGER NEEDED; authority is assumed to be false; so for now this is the same as domain[]
//	private Location globalLocation;
//	private Location localLoc; 
	private int knowledgeSize = 0;

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
	 * localKnowledgeIdxs 	- the String representation of ALL units' knowledge.  ALL other units' knowledge is needed so that we 
	 *						  can assign overlapping knowledge to others' known elements
	 * knowledgeOverlapSize - the number of other units' knowledge elements this unit is knowledgeable about -- extent of shared 
	 *						  domain knowledge
	 * called by Organization: units[i] = new Unit(i, Globals.unitNames[i], Globals.domainDistributionCounts, Globals.localKnowledgeIndices, Globals.knowledgeOverlapIndex[i]);
	 **/
	public Unit(int idx, String name, int[] domainDistributionCnts, String localKnowledgeIdxs, int knowledgeOverlapSize) {
		// set unit index and name
		index = idx;
		unitName = name;

		// set control domain; e.g., domainDistributionCnts = [4,8,4]
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

		// set local knowledge index -- what the unit knows (within and outside of knowledge domain)
		// 1,1,0,0,0,0;0,0,1,1,0,0;0,0,0,0,1,1 -> [[1,1,0,0,0,0],[0,0,1,1,0,0],[0,0,0,0,1,1]]
		// String[] StrLocalKnowledgeIndices = localKnowledgeIdxs.split(";");
		// String[] localDomainKnowledges = new String[StrLocalKnowledgeIndices.length];
		// for (int i = 0; i < StrLocalKnowledgeIndices.length; i++) {
		// 	localKnowledgeIndices[i] = StrLocalKnowledgeIndices[i];
		// }

		boolean[] tmpOthersKnowledge = new boolean[Globals.N];
		for (int i = 0; i < localKnowledgeIdxs.split(";").length; i++) {
			if (index == i) {
				for (int j = 0; j < localKnowledgeIdxs.split(";")[i].split(",").length; j++) {
					if (localKnowledgeIdxs.split(";")[i].split(",")[j].equals("1")) {
						knowledge[j] = true;
					}
				}
			}
		}

		for (int i = 0; i < localKnowledgeIdxs.split(";").length; i++) {
			if (index != i) {
				for (int j = 0; j < localKnowledgeIdxs.split(";")[i].split(",").length; j++) {
					if (localKnowledgeIdxs.split(";")[i].split(",")[j].equals("1")) {
						if (!knowledge[j]) {
							tmpOthersKnowledge[j] = true;
						}
					}
				}
			}
		}

		for (int i = 0; i < StrLocalKnowledgeIndex.length; i++) {
			localKnowledgeIndex[i] = Integer.parseInt(StrLocalKnowledgeIndex[i]);
			knowledgeSize++;  // no longer need for kdistribution in Globals/conf
		}
		if (Globals.debug) { System.out.println("knowledge index created for " + unitName); }

		// set overlapIndex -- what the unit knows within other's knowledge domain

		// set overlapIndex
		int[] temp = Globals.rand.nextUniqueIntBetween(0, (Globals.N - localKnowledgeSize) - 1, Globals.isOverlap); // for infoSys -> begin = 0; end = Globals.N / 2 - 1
		System.arraycopy(temp, 0, overlapIndex, 0, temp.length);
		if (Globals.debug) { System.out.println("overlap inddex created for infoSys"); }
		// set knowledge & control 
		for (int i = 0; i < Globals.N; i++) { knowledge[i] = false; control[i] = false; } // first set everything to false;
		if (Globals.debug) { System.out.println("knowledge and control reset for infoSys"); }
		for (int i = 0; i < localKnowledgeIndex.length; i++) { knowledge[localKnowledgeIndex[i]] = true; control[localKnowledgeIndex[i]] = true; }
		if (Globals.debug) { System.out.println("knowledge and control created for infoSys"); }
		for (int i = 0; i < overlapIndex.length; i++) { 
			knowledge[overlapIndex[i]] = true;
			if (Globals.authority) { control[overlapIndex[i]] = true; }
		}
		// set control
		if (Globals.debug) { System.out.println("overlap set for infoSys"); }
		setLocation(globalLoc);
		if (Globals.debug) { System.out.println("set location for infoSys"); }
		resetSearchHistory();
		if (Globals.debug) { System.out.println("reset search history for infoSys"); }
		
	}

	
}