package game.states.fleeing;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Chargeable;
import game.products.minerals.Fire;

import java.util.List;

/**
 * Evaluates whether a location is safe for a fleeing SporeBeast.
 */
public class SafetyEvaluator {

    private static final int MINIMUM_THREAT_DISTANCE = 3;

    /**
     * Determines whether a location avoids environmental hazards and threats.
     *
     * @param location candidate location
     * @param threatLocations locations occupied by known threats
     * @return true if the location is safe
     */
    public boolean isSafe(Location location, List<Location> threatLocations) {
        return !containsHazard(location)
                && nearestThreatDistance(location, threatLocations)
                >= MINIMUM_THREAT_DISTANCE;
    }

    /**
     * Calculates the Chebyshev distance to the nearest known threat.
     *
     * @param location location being evaluated
     * @param threatLocations locations occupied by known threats
     * @return distance to the nearest threat, or Integer.MAX_VALUE if none exist
     */
    public int nearestThreatDistance(
            Location location,
            List<Location> threatLocations) {
        int nearestDistance = Integer.MAX_VALUE;

        for (Location threatLocation : threatLocations) {
            int distance = Math.max(
                    Math.abs(location.x() - threatLocation.x()),
                    Math.abs(location.y() - threatLocation.y()));
            nearestDistance = Math.min(nearestDistance, distance);
        }

        return nearestDistance;
    }

    /**
     * Checks whether a location contains fire or charged ground.
     *
     * @param location location being evaluated
     * @return true if the location is hazardous
     */
    private boolean containsHazard(Location location) {
        if (location.getGround() instanceof Chargeable chargeable
                && chargeable.isCharged()) {
            return true;
        }

        for (Item item : location.getItems()) {
            if (item instanceof Fire) {
                return true;
            }
        }

        return false;
    }
}
