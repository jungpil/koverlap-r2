package obj;

import util.Globals;

public class Neighbor {
	Location location;
	Location focalUnitLocation;
	int probabilityWeight;
	double minProbability;
	double maxProbability;	

	public Neighbor(String[] locationString, String[] focalLoc, int weight) {
		location = new Location(locationString);
		focalUnitLocation = new Location(focalLoc);
		probabilityWeight = weight;
		Globals.debug.println("new Neighbor for " + localUnitLocation.toString() + " as " + location.toString() + " with weight " + weight + " created");
	}

}