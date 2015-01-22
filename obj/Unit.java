package obj;

import java.util.Collections;
import java.util.Vector;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import util.Globals;
import util.Debug;

public class Unit {
	private int index;
	private String unitName;
//	private int localKnowledgeIndex[] = new int[Globals.N / 2]; // [commented 3/24/12]
	/**
	 * domain  			- what elements are within the purview of a unit
	 * fullKnowledge 	- what the unit knows; (includes overlapping knowledge of other domains) --> withinDomainOwnKnowledge OR(+) outsideDomainOwnKnowledge
	 * withinDomainOwnKnowledge		- what the unit knows in own domain; --> domain AND fullKnowledge
	 * outsideDomainOwnKnowledge 	- what the unit knows in others' domains; 
	 * withinDomainOthersKnowledge  - what other units know of my own domain;
	 **/
	private boolean[] domain;
	private boolean[] fullKnowledge[];
	private boolean[] withinDomainOwnKnowledge;
	private boolean[] outsideDomainOwnKnowledge;
	private int[] withinDomainOthersKnowledge;
	private double[] selectionProbabilities;

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
	public Unit(int idx, String name, Location loc, int[] domainDistributionCnts, String knowledgeIdxs) {
		// set unit index and name
		index = idx;
		unitName = name;

		// set unit's knowledge domain (control) ---- MUST BE DONE IN THIS ORDER domain[] needs to be set before knowledge can be set
		setDomain(domainDistributionCnts); // set own knowledge domain 
		setKnowledges(knowledgeIdxs);

		// set neighbors for current location (init); @note: no need to save location; only need location info to set neighbors
		resetSearchHistory(loc); // initialize nieghbor vector and sets neighbors

		Debug.println("Unit " + idx + " (" + name + ") initiated with location " + loc.toStrin() 
								+ ", withinDomainOwnKnowledge " + Debug.arrayToString(withinDomainOwnKnowledge) 
								+ ", outsideDomainOwnKnowledge " + Debug.arrayToString(outsideDomainOwnKnowledge) 
								+ ", withinDomainOthersKnowledge " + Debug.arrayToString(withinDomainOthersKnowledge) 
								+ ", domain " + Debug.arrayToString(domain));
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

	public Location getRecommendation(Location loc) {
		// need to take care of 2 situations: experiential search vs. comprehensive search
		//Landscape.getFitness(Location l, boolean[] know) {

		// get own current perceived fitness value
		double currentFitness = Globals.landscape.getFitness(loc, fullKnowledge);

		// for now we'll just implement experiential search
		// get a random neighbor to consider -- remove one from set of size neighbors.size()
		// @todo: HERE I'M GETTING A RANDOM NEIGHBOR, BUT I NEED TO GET A PROBABILITY WEIGHTED RANDOM NEIGHBOR!!!
		Location neighbor = (Location)neighbors.remove(Globals.rand.nextInt(neighbors.size()));
		double neighborFitness = Globals.landscape.getFitness(neighbor, fullKnowledge);

		if (neighborFitness > currentFitness) {
			
			return neighbor;
		} else {
			return null;
		}
//		moveTo = null;
// //		boolean success = false;
// 		int numRemainingNeighbors = neighbors.size();
// 		int r = Globals.rand.nextInt(numRemainingNeighbors);
// 		Location neighbor = (Location)neighbors.remove(r); // need to find global location for neighbor as well
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

	private Location pickNeighbor() {
		// implement neighbor selection method here
		// Globals.neighborSelectionApproach = {"random", "myknowledge", "othersknowledge", "cross"}
		// unfortunately Java doesn't support String-based switch statement

		// // ACTUALLY DON'T DO IT HERE.  WE NEED TO DO IT WHEN SETTING THE WEIGHTS
		// if (Globals.neighborSelectionApproach.equals("random")) {
		// 	// pick random neighbor from neighbor set and return
		// 	return (Location)neighbors.remove(Globals.rand.nextInt(neighbors.size()));
		// } else if (Globals.neighborSelectionApproach.equals("myknowledge")) {
		// 	// give more probability weight to neighbor if 
		// } else if (Globals.neighborSelectionApproach.equals("othersknowledge")) {
		// } else if (Globals.neighborSelectionApproach.equals("cross")) {

		// }
		Location neighbor = (Location)neighbors.remove(Globals.rand.nextInt(neighbors.size()));
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


	private void setNeighborSelectionProbabilities(Location loc) {
		// implementation neighbor selection probability computation
		// Globals.neighborSelectionApproach = {"random", "myknowledge", "othersknowledge", "cross"}
		// 1. random - pick a random neighbor; neighbors only perturb within domain elements
		// 2. myknowledge - pick a random neighbor; neighbor set also includes perturbations of other domain elements if focal unit has shared knowledge 
		//                  -> if I know, then I can consider implications of those changes as well
		// 3. othersknowledge - give preferential weight (weighted probability by Globals.preferenceWeightages) for elements within my domain for which other units have shared knowledge
		//                     -> if other unit knows an element, then focus on setting that first; neighbor set is same as random
		// 4. cross - combination of myknowledge and othersknowledge - preferential weightage + expanded neighborset
		
		// init selectionProbabilities array to fit neighbor vector size (to account for previously visited/discarded locations)
		selectionProbabilities = new double[neighbors.size()];

		// ACTUALLY DON'T DO IT HERE.  WE NEED TO DO IT WHEN SETTING THE WEIGHTS 
		// we could combine random + myknowledge and othersknowledge + cross but since these may change, let's keep them separate for now
		if (Globals.neighborSelectionApproach.equals("random")) { // option 0
			// pick random neighbor from neighbor set and return
			for (int i = 0; i < selectionProbabilities.length; i++) selectionProbabilities[i] = 1d / selectionProbabilities.length;
		} else if (Globals.neighborSelectionApproach.equals("myknowledge")) { // option 2
			// also consider neighbors that alter what the focal unit knows (setNeighbors must change!!)
			for (int i = 0; i < selectionProbabilities.length; i++) selectionProbabilities[i] = 1d / selectionProbabilities.length;
		} else if (Globals.neighborSelectionApproach.equals("othersknowledge")) { // option 1
			// give preferential weight to other units' knowledge of my domain
			// check neighbors diff with current location and 

			for (int i = 0; i < neighbors.size(); i++) {
				Location nb = (Location)neighbors.get(i);
				int[] countDiff = new int[Globals.N];
				for (int j = 0; j < countDiff.length; j++) countDiff[j] = 1; // initialize with default value = 1

				// need to find neighbor which has different value (from current location) for elements 
				for (int j = 0; j < Globals.N; j++) {
					if (!nb.getLocationAt(j).equals(loc.getLocationAt(j))) { // element index
						if (withinDomainOthersKnowledge[j] > 0) { // some other unit knows this element
							countDiff[j] *= withinDomainOthersKnowledge[j] * Globals.preferentialWeightage;
						} // else { countDiff[j] = 1; } // if other unit does not know this element j
					} // else { countDiff[j] = 1; } if value of this element for considered neighbor is the same as current location value for this element
				}
			}
		} else if (Globals.neighborSelectionApproach.equals("cross")) { // option 4
			// give preferential weight to other units' knowledge of my domain
			// check neighbors diff with current location and 

			for (int i = 0; i < neighbors.size(); i++) {
				Location nb = (Location)neighbors.get(i);
				int[] countDiff = new int[Globals.N];
				for (int j = 0; j < countDiff.length; j++) countDiff[j] = 1; // initialize with default value = 1

				// need to find neighbor which has different value (from current location) for elements 
				for (int j = 0; j < Globals.N; j++) {
					if (!nb.getLocationAt(j).equals(loc.getLocationAt(j))) { // element index
						if (withinDomainOthersKnowledge[j] > 0) { // some other unit knows this element
							countDiff[j] *= withinDomainOthersKnowledge[j] * Globals.preferentialWeightage;
						} // else { countDiff[j] = 1; } // if other unit does not know this element j
					} // else { countDiff[j] = 1; } if value of this element for considered neighbor is the same as current location value for this element
				}
			}
		}
	}


	//knoweldgeIdxs = "1,1,0,0,0,0;0,0,1,1,0,0;0,0,0,0,1,1" -> [[1,1,0,0,0,0],[0,0,1,1,0,0],[0,0,0,0,1,1]]
	private void setKnowledges(String knowledgeIdxs) {
		// note: setDomain(domainDistributionCounts) must be completed before this method -> domain[] has to be set 
		// done in Constructor 
		fullKnowledge = new boolean[Globals.N]; // initialized to all false 
		withinDomainOwnKnowledge = new boolean[Globals.N]; // initialized to all false 
		outsideDomainOwnKnowledge = new boolean[Globals.N]; // initialized to all false 
		withinDomainOthersKnowledge = new int[Globals.N]; // innitialize to all zero (0)

		// 1.  set fullKnowledge[] -- what the unit knows including overlapping knowledge of others' domains
		for (int i = 0; i < knowledgeIdxs.split(";").length; i++) {
			if (index == i) { // for focal unit
				for (int j = 0; j < knowledgeIdxs.split(";")[i].split(",").length; j++) {
					if (knowledgeIdxs.split(";")[i].split(",")[j].equals("1")) {
						fullKnowledge[j] = true;
					}
				}
			} else { // for other units
				for (int j = 0; j < knowledgeIdxs.split(";")[i].split(",").length; j++) {
					if (knowledgeIdxs.split(";")[i].split(",")[j].equals("1")) {
						if (domain[j]) withinDomainOthersKnowledge[j]++;
					}
				}
			}
		}
		// 2. distinguish between own and others' knowledge
		for (int i = 0; i < fullKnowledge.length; i++) {
			if (fullKnoweldge[i]) { // if unit "knows"
				if (domain[i]) { // if within unit's own domain 
					withinDomainOwnKnowledge[i] = true;
				} else { // if outside of unit's own domain 
					outsideDomainOwnKnowledge[i] = true;
				}
			}
		}
	}

	// get own knowledge size -- how many elements the unit knows within own domain
	public int getWithinDomainOwnKnowledgeSize() {
		int count = 0;
		for (boolean boolValue : withinDomainOwnKnowledge) if (boolValue) count++;
		return count;
	}

	// get overlapping knowledge size -- how many elements the unit knows outside own domain
	public int getOutsideDomainOwnKnowledge() {
		int count = 0;
		for (boolean boolValue : outsideDomainOwnKnowledge) if (boolValue) count++;
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

	public boolean hasNeighbors() {
		if (neighbors.size() > 0) {
			return true;
		} else {
			return false;
		}
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

//	private void setNeighbors() {
//		for (int i = 0; i < Globals.N; i++) {
//			String[] neighborLocString = new String[Globals.N];
//			boolean add = false;
//			for (int j = 0; j < Globals.N; j++) {
//				if (i == j) {
//					if (localLoc.getLocationAt(j).equals("1")) {
//						neighborLocString[j] = "0"; add = true;
//					} else if (localLoc.getLocationAt(j).equals("0")) {
//						neighborLocString[j] = "1"; add = true;
//					} // else locationAt is blank so do nothing
//				} else { // all other i != j
//					neighborLocString[j] = localLoc.getLocationAt(j);
//				}
//			}
//			if (add) { neighbors.add(new Location(neighborLocString)); }
//		}
//		//Collections.shuffle(neighbors);  // shuffle so that order of retrieval is randomized
//		
//		Debug.println("Neighbors for " + unitName); 
//		printNeighbors(); 
//	}

	/**
	 *  For a given location and maxDistance, fill neighbor vector with neighbors of variations in "maxDistance" elements
	 *  i.e., if maxDistance = 2, then add all 1-off neighbors and all 2-off neighbors
	 *  Note: ownKnowledge is used as mask for neighbors
	 */
	private void setNeighbors(Location loc, int maxDistance) {
		// get the combination of knowledge combinations for max distance for own knowledge
		List<Integer> ownNeighborCombinationKnowledgeIndices = new ArrayList<Integer>();
		for (int i = 0; i < withinDomainOwnKnowledge.length; i++) {
			if (withinDomainOwnKnowledge[i]) ownNeighborCombinationKnowledgeIndices.add(i);
		}

		// get all of the combinations 
		// e.g., if withinDomainOwnKnowledge = [t, t, t, t, f, f, t, f] and maxDistance = 1
		// then neighborIndexCombinations = [[], [0], [1], [2], [3], [6]]; NOTE the empty one (always returned)
		// if maxDistance = 2
		// then neighborIndexCombinations = [[], [0], [1], [2], [3], [6], [0,1], [0,2], [0,3], [0,6], [1,2], [1,3], [1,6], [2,3], [2,6], [3,6]]; 
		Set<Set<Integer>> ownNeighborIndexCombinations = getCombinationsFor(ownNeighborCombinationKnowledgeIndices, maxDistance);

		if ((Globals.neighborSelectionApproach.equals("myknowledge")) || Globals.neighborSelectionApproach.equals("cross")) { // option 2 or 4
			// get the combination of knowledge combinations for max distance for shared knowledge in other's domain
			List<Integer> othersNeighborCombinationKnowledgeIndices = new ArrayList<Integer>();
			for (int i = 0; i < outsideDomainOwnKnowledge.length; i++) {
				if (outsideDomainOwnKnowledge[i]) othersNeighborCombinationKnowledgeIndices.add(i);
			}
			Set<Set<Integer>> othersNeighborIndexCombinations = getCombinationsFor(othersNeighborCombinationKnowledgeIndices, othersNeighborCombinationKnowledgeIndices.size());
		}

		for (Set<Integer> combo : ownNeighborIndexCombinations) {
			if (combo.isEmpty()) {
				// System.out.println("empty");
				// do nothing, there is always one empty one that is returned -> ignore
			} else {
				String[] neighborLocString = loc.getLocation();

				for (Integer comboInt : combo) { // loop through each Int in neighborCombination
					neighborLocString[comboInt] = flip(neighborLocString[comboInt]);
					if (othersKnowledge[comboInt]) { // if shared knowledge (if I know the other unit's knowledge)
													 // think about incorporating what the unit knows about what the other unit knows (DIFFICULT)
						//weight += Globals.sharedKnowledgePreferenceWeight;
					}
				}

				if ((Globals.neighborSelectionApproach.equals("myknowledge")) || Globals.neighborSelectionApproach.equals("cross")) { // option 2 or 4
					for (Set<Integer> sharedCombo : othersNeighborIndexCombinations) {
						if (!sharedCombo.isEmpty()) {
							for (Integer sharedComboInt : sharedCombo) {
								neighborLocString[sharedComboInt] = flip(neighborLocString[sharedComboInt]);
							}
						}
					}
				}
	
				neighbors.add(new Location(neighborLocString));
				Globals.debug.println("add neighbor: " + Globals.arrayToString(neighborLocString));
				// neighbors.add(new Neighbor(neighborLocString, loc.getLocation(), weight));
			}
		}
		
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

	public void printAllNeighbors() {
		
	}
	
	private Set<Set<Integer>> getCombinationsFor(List<Integer> group, int subsetSize) {
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

	private void resetSearchHistory(Location loc) {
		neighbors = new Vector<Location>();
		setNeighbors(loc, Globals.numAlternative);
	}

}
