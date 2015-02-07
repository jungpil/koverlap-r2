package obj;

import util.Globals;

/**
 *   - determine current Unit to do search
 *   - have unit submit recommendation
 *   
 */

public class Organization {
//	private int period;  // NO NEED ANYMORE
	protected int index; 
	protected String orgType;
	protected Location location; 
	protected Unit[] units = new Unit[Globals.numUnits];  // initialize number of DMUs to Globals.numSubOrgs

	protected int[] searchStatus = new int[Globals.numUnits]; // -2 for not started; -1 for local optimum; 0 for failed search; 1 for moved
	protected boolean completed;
	protected int lastSearchingUnitIdx = -1;
	protected int next = -1; // focal DMU (whose turn is it to search)?
	protected boolean lastPrinted = false;


	public Organization(int idx) {
		index = idx;
		// orgType = "whatever"; set by subclass
		location = new Location(); // random location to start with
		/**
		 * units[0] = new Business(location, Globals.kdists[0]);
		 * searchStatus[0] = -2;    //  DO I NEED THIS?
		 * Debug.println("Business DMU " + idx + " created");
		 * units[1] = new InfoSys(location, Globals.kdists[1]);
		 * searchStatus[1] = -2;    //  DO I NEED THIS?
		 * Debug.println("InfoSys DMU " + idx + " created");
		*/
		
		// no need to make specific for Business/InfoSys -- make general
		for (int i = 0; i < Globals.numUnits; i++) {
			//units[i] = new DMU(i, Globals.unitNames[i], location, Globals.kdists[i], Globals.localKnowledgeIndex[i], Globals.knowledgeOverlapIndex[i]);
			units[i] = new Unit(i, Globals.unitNames[i], location, Globals.domainDistributionsCounts, Globals.localKnowledgeIndices);
		}
		/* period = 0; */
		completed = false;
		//lastDMU = -2;
	}
	
	public boolean finished() {
		return completed;
	}

	public void run() {
		// 1.  determine current unit to search
		//int focalUnitIdx = determineFocalUnitIdx();
		// check with last unit that searched
		//     if last unit recommended move -> current unit is other unit
		//     if last unit recommended stay 
		//         if still has neighbors -> current unit is the same unit
		//         if no more neighbors -> current unit is other unit
		//     how do we find completed? --> cumulative number of no move units = numunits
		// have unit submit recommendation
		// action -> move or stay



	} // implemented by subclasses
	
	protected int determineFocalUnitIdx() {
		// check with last unit that searched
		//     if last unit recommended move -> current unit is other unit
		//     if last unit recommended stay 
		//         if still has neighbors -> current unit is the same unit
		//         if no more neighbors -> current unit is other unit
		//     @todo how do we find completed? --> cumulative number of no move units = numunits

		int idx = 0; 
		try { // arrayIndexOutOfBounds Exception since lastSearchingUnitIdx initialized to -1
			if (units[lastSearchingUnitIdx].decisionIsMove()) { // last unit moved
				idx = (lastSearchingUnitIdx+1) % units.length; // modulus loops back to 0
			} else { // last unit couldn't move
				if (units[lastSearchingUnitIdx].hasNeighbors()) { // still has neighbors
					idx = lastSearchingUnitIdx;
				} else { // no more neighbors
					idx = (lastSearchingUnitIdx+1) % units.length; // modulus loops back to 0
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// ignore, exception caught to simplify identification of start since lastSearchingUnitIdx = -1 (from Class initialize)
			// return idx = 0;
		}
		return idx;
	}

	public double getOrgFitness() {
		return Globals.landscape.getFitness(location);
	}

//	protected void updateLocation(Location dmuLocalLoc, int dmuIndex) {
//		// get the filter from the DMU and update the location 
//		boolean filter[] = new boolean[Globals.N];
//		System.arraycopy(units[dmuIndex].getControlFilter(), 0, filter, 0, Globals.N);
//		String[] newGlobalLocation = new String[Globals.N];
//		for (int i = 0; i < Globals.N; i++) {
//			if (filter[i]) { 
//				newGlobalLocation[i] = dmuLocalLoc.getLocationAt(i);
//			} else {
//				newGlobalLocation[i] = location.getLocationAt(i);
//			}
//		}
//		location.setLocation(newGlobalLocation);		
//	}
		
	// PRINTERS
	public void printDetails(int period) {
//		double globalFitness = Globals.landscape.getFitness(location);
//		double[] localFitness = new double[Globals.numUnits];
//		for (int i = 0; i < Globals.numUnits; i++) {
//			localFitness[i] = units[i].getFitness();
//		}
//		String searchStatusString = "";
//		String localFitnessString = "";
//		for (int i = 0; i < Globals.numUnits; i++) {
//			searchStatusString += searchStatus[i] + "\t";
//			localFitnessString += localFitness[i] + "\t";
//		}
//		
//		if (!completed) {
//			Globals.out.println(period + "\t" + index + "\t" + searchStatusString + next + "\t" + location.toString() + "\t" + localFitnessString + globalFitness);
//		} else {
//			if (!lastPrinted) {
//				Globals.out.println(period + "\t" + index + "\t" + searchStatusString + next + "\t" + location.toString() + "\t" + localFitnessString + globalFitness);
//				lastPrinted = true;
//			}
//		}
	}
	
	public Unit getUnit(int i) {
		return units[i];
	}
	
	public void printUnitNeighbors(int i) {
		System.out.println("unit: " + i);
		units[i].printAllNeighbors();
	}
	
	public void printLocation() {
		System.out.println(location.toString());
	}
	
	public String toString() {
		String toString = orgType + "\t" + location.toString() + "\t" + Globals.landscape.getFitness(location);
		return toString;
	}

	/** main method for unit testing */
	public static void main(String args[]) {
		Globals.createLandscape(0);
//		Location l = new Location();
//		System.out.println("initial location: " + l.toString());
		Organization o = new Organization(0);
		o.printLocation();
//		o.printDMUNeighbors(0);
//		o.printDMUNeighbors(1);
		
	}
	
}
