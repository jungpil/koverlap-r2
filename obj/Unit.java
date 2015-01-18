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
	private boolean move; // if unit's decision is to move to new location, set this to true

//	private boolean move; 
	//private Vector<Location> neighbors;
	private Vector<Neighbor> neighbors;




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
		 * organization should search by delegating recommendation to units; units don't need (or know) global location knowledge
		// if (Globals.debug) { System.out.println("overlap set for infoSys"); }
		// setLocation(globalLoc);
		// if (Globals.debug) { System.out.println("set location for infoSys"); }
		// resetSearchHistory();
		// if (Globals.debug) { System.out.println("reset search history for infoSys"); }
		*/
		
	}

	public String getRecommendation(Location loc) {
		// need to take care of 2 situations: experiential search vs. comprehensive search
		//Landscape.getFitness(Location l, boolean[] know) {

		// get own current perceived fitness value
		double currentFitness = Globals.landscape.getFitness(loc, fullKnowledge);

		moveTo = null;
//		boolean success = false;
		int numRemainingNeighbors = neighbors.size();
		int r = Globals.rand.nextInt(numRemainingNeighbors);
		Location neighbor = (Location)neighbors.remove(r); // need to find global location for neighbor as well
		String[] neighborGlobalLocString = new String[Globals.N];
		for (int i = 0; i < Globals.N; i++) {
			 if (neighbor.getLocationAt(i).equals(" ")) {
				 neighborGlobalLocString[i] = globalLocation.getLocationAt(i);
			 } else {
				 neighborGlobalLocString[i] = neighbor.getLocationAt(i);
			 }
		}
		Location neighborGlobalLocation = new Location(neighborGlobalLocString);
		
		double localFitness = 0d;
		double neighborFitness = 0d;
		if (Globals.localAssessment.equals("gl2000")) {
			localFitness = Globals.landscape.getFitness(localLoc);
			neighborFitness = Globals.landscape.getFitness(neighbor);
		} else if (Globals.localAssessment.equals("ac2010")) {
			localFitness = Globals.landscape.getFitness(globalLocation, knowledge);
			neighborFitness = Globals.landscape.getFitness(neighborGlobalLocation, knowledge); // need to find global location for neighbor as well
		}
//		System.out.println("localFitness:\t" + localFitness);
//		System.out.println("neighborFitness(" + r + "):\t" + neighborFitness);

		if (neighborFitness > localFitness) {
			// replace localLoc with neighbor & reset tried vector (no need to create new Location object)
//			localLoc.setLocation(neighbor); // set it now or later?
			// since moveTo was null before, we need to initialize it first and then set it's location
			moveTo = new Location(neighbor);
//			moveTo.setLocation(neighbor);
			resetSearchHistory();
//			success = true;
		}
		return moveTo;
	}

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
		int count = 0;
		for (boolean boolValue : ownKnowledge) if (boolValue) count++;
		return count;
	}

	// get overlapping knowledge size -- how many elements the unit knows outside own domain
	public int getOverlappingKnowedgeSize() {
		int count = 0;
		for (boolean boolValue : othersKnowledge) if (boolValue) count++;
		return count;
	}

	// get full knowledge size -- how many elements the unit knows altogether
	public int getFullKnowledgeSize() {
		int count = 0;
		for (boolean boolValue : fullKnowledge) if (boolValue) count++;
		return count;
	}

	public boolean decisionIsMove() {
		return move;
	}

	private void setNeighbors(Location loc) {
		for (int i = 0; i < Globals.N; i++) {
			String[] strNeighborLocation = new String[Globals.N];
			boolean add = false;
			for (int j = 0; j < Globals.N; j++) {
				if (i == j) { // for focal di
					// which are the blank ones?  knowledge? => cannot use localLoc, we're using globalLoc

				}
			}
		}
	}

	private void setNeighbors() {
		for (int i = 0; i < Globals.N; i++) {
			String[] neighborLocString = new String[Globals.N];
			boolean add = false;
			for (int j = 0; j < Globals.N; j++) {
				if (i == j) {
					if (localLoc.getLocationAt(j).equals("1")) {
						neighborLocString[j] = "0"; add = true;
					} else if (localLoc.getLocationAt(j).equals("0")) {
						neighborLocString[j] = "1"; add = true;
					} // else locationAt is blank so do nothing
				} else { // all other i != j
					neighborLocString[j] = localLoc.getLocationAt(j);
				}
			}
			if (add) { neighbors.add(new Location(neighborLocString)); }
		}
		//Collections.shuffle(neighbors);  // shuffle so that order of retrieval is randomized
		
		if (Globals.debug) { System.out.println("Neighbors for " + unitName); printNeighbors(); }
	}

	/**
	 *  For a given location and maxDistance, fill neighbor vector with neighbors of variations in "maxDistance" elements
	 *  i.e., if maxDistance = 2, then add all 1-off neighbors and all 2-off neighbors
	 *  Note: ownKnowledge is used as mask for neighbors
	 */
	private void setNeighbors(Location loc, int maxDistance) {
		// get the combination of knowledge combinations for max distance
		List<Integer> neighborCombinationKnowledgeIndices = new ArrayList<Integer>();
		for (int i = 0; i < fullKnowledge.length; i++) {
			if (fullKnowledge[i]) neighborCombinationKnowledgeIndices.add(i);
		}

		// get all of the combinations 
		// e.g., if fullknowledge = [t, t, t, t, f, f, t, f] and maxDistance = 1
		// then neighborIndexCombinations = [[], [0], [1], [2], [3], [6]]; NOTE the empty one (always returned)
		// if maxDistance = 2
		// then neighborIndexCombinations = [[], [0], [1], [2], [3], [6], [0,1], [0,2], [0,3], [0,6], [1,2], [1,3], [1,6], [2,3], [2,6], [3,6]]; 
		Set<Set<Integer>> neighborIndexCombinations = getCombinationsFor(neighborCombinationKnowledgeIndices, maxDistance);

		for (Set<Integer> combo : neighborIndexCombinations) {
			if (combo.isEmpty()) {
				//System.out.println("empty");
				// do nothing
			} else {
				String[] neighborLocString = loc.getLocation();
				int weight = 1; 
				for (Integer comboInt : combo) { // loop through each Int in neighborCombination
					neighborLocString[comboInt] = flip(neighborLocString[comboInt]);
					if (othersKnowledge[comboInt]) { // if shared knowledge (if I know the other unit's knowledge)
													 // think about incorporating what the unit knows about what the other unit knows (DIFFICULT)
						//weight += Globals.sharedKnowledgePreferenceWeight;
					}
				}
				neighbors.add(new Neighbor(neighborLocString, loc.getLocation(), weight));
			}
		}
		// set neighborProbabilities?
		
	}
	
	private String flip(String value) {
		if (value.equals("0")) {
			return "1"; 
		} else if (value.equals("1")) {
			return "0"; 
		} else {
			Globals.debug.println("cannot flip empty value");
			return " ";
		}
	}

	private static Set<Set<Integer>> getCombinationsFor(List<Integer> group, int subsetSize) {
    	Set<Set<Integer>> resultingCombinations = new HashSet<Set<Integer>> ();
    	int totalSize=group.size();
    	if (subsetSize > totalSize) {
    		subsetSize = totalSize;
    	}
    	if (subsetSize == 0) {
    		resultingCombinations.add(new HashSet<Integer>());
        	//emptySet(resultingCombinations);
    	} else if (subsetSize <= totalSize) {
        	List<Integer> remainingElements = new ArrayList<Integer> (group);
        	// Integer X = popLast(remainingElements);
        	Integer X = remainingElements.remove(remainingElements.size()-1);

        	Set<Set<Integer>> combinationsExclusiveX = getCombinationsFor(remainingElements, subsetSize);
        	Set<Set<Integer>> combinationsInclusiveX = getCombinationsFor(remainingElements, subsetSize-1);
        	for (Set<Integer> combination : combinationsInclusiveX) {
            	combination.add(X);
        	}
        	resultingCombinations.addAll(combinationsExclusiveX);
        	resultingCombinations.addAll(combinationsInclusiveX);
    	} 
    	return resultingCombinations;
	}

}
