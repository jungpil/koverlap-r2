package obj;

import util.Debug;
import util.Globals;

/**
 * Agile organization
 * - iterative / incremental decision making
 * 
 * Members inherited from parent (Organization)
 *	int index; // org index
 *	String orgType; // org type
 *	Location location;  // location
 *	Unit[] units;  // subunits
 *	int[] searchStatus = new int[Globals.numUnits]; // -2 for not started; -1 for local optimum; 0 for failed search; 1 for moved
 *	boolean completed; // organization has completed (can no longer move)
 *	int lastSearchingUnitIdx = -1; // which unit searched last
 *	int next = -1; // focal DMU (whose turn is it to search)?
 *	boolean lastPrinted = false; // need?
 */
public class AgileDistance extends Organization {
	
	public AgileDistance(int id) {
		super(id);				// constructor of super class (sets location & creates sub-units)
		orgType = "agile";		// set orgType (parent)

		// is there anything structural to set specifically for agile?, if so, do here.
	}
	
	public void run() {
		super.run(); // parent's run method first for any general procedures
		// 1.  determine current unit to search; need to do this before as "completed" is determined during determineFocalUnitIdx
		int focalUnitIdx = determineFocalUnitIdx(); // parent method
		Debug.println("Agile.run() - orgID: " + index + ", focalUnit: " + focalUnitIdx);
		Debug.println("Agile.run: current location: " + location.toString() + "(" + Globals.landscape.getFitness(location) + ")");
		if (!completed) {
			
			// how do we find completed? --> cumulative number of no move units = numunits
			// have unit submit recommendation
			// action -> move or stay (move = valid location; stay = null)
			lastSearchingUnitIdx = focalUnitIdx;
			Location proposedNeighbor = units[focalUnitIdx].getRecommendation(location); // given the current location determine recommendation
			
			if (proposedNeighbor == null) { // unsuccessful search
				Debug.println("proposed neighbor not found");
				units[focalUnitIdx].proposalRejected();
				// do nothing
			} else {
				for (int i = 0; i < Globals.N; i++) {
					if (units[focalUnitIdx].knowDomainAt(i)) {
						location.setLocationValueAt(i, proposedNeighbor.getLocationAt(i)); // move only within domain elements
					}
				}
				units[focalUnitIdx].proposalAccepted();
				
				Debug.println("move to proposed location given knowledge");
				Debug.println("new location: " + location.toString());
			}
		} // if completed: do nothing

	}

//	public void run_old() {
//		if (!completed) {
//			next = next(lastDMU);
//			
//			int other = Math.abs(next - 1);
////			System.out.println("Location: " + location.toString());
//			if (next != -1) {
//				// moveTo is just a localLocation so I need to figure out the full location to update the org's location
//				Location moveTo = units[next].search(); 
//				if (moveTo == null) { // search was unsuccessful
//					if (Globals.debug) { System.out.println("Agile.run(): search unsuccessful (moveTo==null)"); }
//					// check if next is at local optimum
//					if (units[next].isLocalOptimum()) {
//						// if yes, 
//						//		set own (next) searchstatus to -1
//						searchStatus[next] = -1;
//						// 		check if other's search status == -2
//						if (searchStatus[other] == -2) {
//							//		if yes, 
//							//				reset other's search history
//							units[other].resetHistory();
//						}
//					} else {
//						// if no, 
//						//		set own (next) search status to 0
//						searchStatus[next] = 0;
//					}
//					
//				} else { // search was successful and moveTo is the new localLocation for the business unit (DMU) -- business or infosys
//					if (Globals.debug) { System.out.println("Agile.run(): search successful (moveTo==" + moveTo.toString() + ")"); }
//					// set Org's location to match moveTo 
//					updateLocation(moveTo, next);
//					// update own (next) localLocation
//					units[next].setLocation(location);
//					// update other's localLocaiotn
//					units[other].setLocation(location);
//					// set own (next) searchstatus to 1
//					searchStatus[next] = 1;
//					// reset own (next) searchhistory
//					units[next].resetHistory();
//					// reset other's searchhistory
//					units[other].resetHistory();
//					// set other's searchstatus to -2;
//					searchStatus[other] = -2;
//				}
//			} else {
//				completed = true;
//				// this should never happen --> could not find next move (i.e., both bus and IS are at local optima)
//			}
//			lastDMU = next;
//		}
//	}

//	private int next_old(int last) { 
//		int nextIdx = -1;
//		int other = Math.abs(last - 1);
//
//		if (last == -2) { // simulation hasn't started yet
//			nextIdx = 0; // business should start
//		} else if (last == -1) { // simulation has ended
//			nextIdx = -1;
//		} else { // DMU is still searching
//			if (searchStatus[last] == 0) { 
//				nextIdx = last;
//			} else if (searchStatus[last] == 1) {
//				nextIdx = other;
//			} else if (searchStatus[last] == -1) {
//				if (searchStatus[other] == -1) {
//					nextIdx = -1;
//				} else {
//					nextIdx = other;
//				}
//			}
//		}
////		System.out.println(t + "\t" + last + "\t" + searchStatus[0] + "\t" + searchStatus[1] + "\t" + nextIdx);
//		return nextIdx;
//	}
	
}
