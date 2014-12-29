package obj;

import java.util.Collections;
import java.util.Vector;
import util.Globals;

public class DMU {
	protected String DMUType;
	protected Location globalLocation;
	protected Location localLoc; 
//	protected int localKnowledgeIndex[] = new int[Globals.N / 2]; // [commented 3/24/12]
	protected boolean knowledge[] = new boolean[Globals.N];
	protected boolean control[] = new boolean[Globals.N];
	protected Location moveTo;

//	private int index;
//	private boolean move; 
	private Vector<Location> neighbors;
	
	public DMU() {
		
	}
	
	public DMU(int idx, Location loc, String type) {
//		index = idx; // idx = {0, 1} for {bus, IS}
//		move = false;
		// initialize search history
//		resetSearchHistory(); // empty tried; create new vector of neighbors
//		localFitness = getFitness(location, "local");
	}


	public DMU(int idx, String DMUName, Location loc, int localKnowledgeSize) {
		DMUType = DMUName;
		// set number of pointers to random elements of the InfoSys
//		super();
		// set localKnowledgeIndex
		localKnowledgeIndex = new int[localKnowledgeSize];

		for (int i = Globals.N - localKnowledgeSize; i < Globals.N; i++) {
			localKnowledgeIndex[i - (Globals.N - localKnowledgeSize)] = i;  // for InfoSys -> i = Globals.N / 2 through Globals.N
		}
		if (Globals.debug) { System.out.println("knowledge index created for infoSys"); }

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
	/**
	 * accessors
	 */
	
	public Location getLocation() {
		return localLoc;
	}
	
	public String[] getLocationString() {
		return localLoc.getLocation();
	}
	
	public void setLocation(String[] locationStr) {
		localLoc = new Location(locationStr);
	}
	
	public double getFitness() {
		return Globals.landscape.getFitness(localLoc);
	}
	
	public boolean[] getControlFilter() {
		return control;
	}
	
	// SEARCH
	public Location search() {
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

	public void resetHistory() {
		resetSearchHistory();
	}

	protected void resetSearchHistory() {
		neighbors = new Vector<Location>();
//		if (orgType.equals("joint2")) {
//			setNeighbors2();
//		} else {
			setNeighbors();
//		}
//		printNeighbors();
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
		
		if (Globals.debug) { System.out.println("Neighbors for " + DMUType); printNeighbors(); }
	}

//	public boolean hasMoved() {
//		return move;
//	}
	
	public boolean isLocalOptimum() {
		return neighbors.isEmpty();
	}
	

	private void printNeighbors() {
		for (Location neighbor : neighbors) {
			System.out.println(neighbor.toString());
		}
	}
	
	// debug only
	public void start() {
		resetSearchHistory();
	}

	protected void setLocation(Location loc) {
		globalLocation = loc;
		String[] localLocString = new String[Globals.N];
		
		for (int i = 0; i < Globals.N; i++) {
			if (knowledge[i]) {
				localLocString[i] = loc.getLocationAt(i);
			} else {
				localLocString[i] = " ";
			}
		}
		localLoc = new Location(localLocString);
	}
		
	public void printAllNeighbors() {
		printNeighbors();
	}

	// for debug purposes only
	public static void main(String args[]) {
		Globals.createLandscape(0);
		Location l = new Location();
		System.out.println(l.toString());
		Business b = new Business(l);
		InfoSys is = new InfoSys(l);
		System.out.println(b.getLocation().toString());
		System.out.println(is.getLocation().toString());
	}
}
