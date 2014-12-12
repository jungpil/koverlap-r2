package obj;

import util.Globals;

public class Business extends DMU {
	protected int overlapIndex[] = new int[Globals.busOverlap];
	// [added 3/24/12]
	protected int localKnowledgeIndex[]; // = new int[Globals.N / 2];

	public Business(Location globalLoc) {
		DMUType = "business";
		// set number of pointers to random elements of the InfoSys
//		super();
		// set localKnowledgeIndex
		// [added 3/24/12]
		localKnowledgeIndex = new int[Globals.N / 2];
		for (int i = 0; i < Globals.N / 2; i++) {
			localKnowledgeIndex[i] = i;  // for InfoSys -> i = Globals.N / 2 through Globals.N
		}
		// set overlapIndex
		int[] temp = Globals.rand.nextUniqueIntBetween(Globals.N / 2, Globals.N - 1, Globals.busOverlap); // for infoSys -> begin = 0; end = Globals.N / 2 - 1
		System.arraycopy(temp, 0, overlapIndex, 0, temp.length);
		// set knowledge & control 
		for (int i = 0; i < Globals.N; i++) { knowledge[i] = false; control[i] = false; } // first set everything to false;
		for (int i = 0; i < localKnowledgeIndex.length; i++) { knowledge[localKnowledgeIndex[i]] = true; control[localKnowledgeIndex[i]] = true; }
		for (int i = 0; i < overlapIndex.length; i++) { 
			knowledge[overlapIndex[i]] = true;
			if (Globals.authority) { control[overlapIndex[i]] = true; }
		}
		// set control
		setLocation(globalLoc);
		resetSearchHistory();
	}

	// [added 3/24/12]
	public Business(Location globalLoc, int localKnowledgeSize) {
		DMUType = "business";
		// set number of pointers to random elements of the InfoSys
//		super();
		// set localKnowledgeIndex
		localKnowledgeIndex = new int[localKnowledgeSize];

		for (int i = 0; i < localKnowledgeSize; i++) {
			localKnowledgeIndex[i] = i;  // for InfoSys -> i = Globals.N / 2 through Globals.N
		}
		// set overlapIndex
		int[] temp = Globals.rand.nextUniqueIntBetween(localKnowledgeSize, Globals.N - 1, Globals.busOverlap); // for infoSys -> begin = 0; end = Globals.N / 2 - 1
		System.arraycopy(temp, 0, overlapIndex, 0, temp.length);
		// set knowledge & control 
		for (int i = 0; i < Globals.N; i++) { knowledge[i] = false; control[i] = false; } // first set everything to false;
		for (int i = 0; i < localKnowledgeIndex.length; i++) { knowledge[localKnowledgeIndex[i]] = true; control[localKnowledgeIndex[i]] = true; }
		for (int i = 0; i < overlapIndex.length; i++) { 
			knowledge[overlapIndex[i]] = true;
			if (Globals.authority) { control[overlapIndex[i]] = true; }
		}
		// set control
		setLocation(globalLoc);
		resetSearchHistory();
	}
}
