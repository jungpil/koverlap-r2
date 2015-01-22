package obj;

import util.Globals;
import util.Debug;

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
		Debug.println("new Neighbor for " + focalUnitLocation.toString() + " as " + location.toString() + " with weight " + weight + " created");
	}

}