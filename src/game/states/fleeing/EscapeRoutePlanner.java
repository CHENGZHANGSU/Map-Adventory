package game.states.fleeing;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Plans a deterministic first step towards the safest reachable location.
 */
public class EscapeRoutePlanner {

    private final SafetyEvaluator safetyEvaluator;

    /**
     * Creates an escape planner using the supplied safety evaluator.
     *
     * @param safetyEvaluator component that evaluates candidate locations
     */
    public EscapeRoutePlanner(SafetyEvaluator safetyEvaluator) {
        this.safetyEvaluator = safetyEvaluator;
    }

    /**
     * Finds the first exit on the best reachable escape route.
     *
     * <p>Candidate locations are ranked by maximum distance from the nearest
     * threat, then shortest route, y-coordinate, and x-coordinate. These
     * stable tie-breakers keep the creature's decision deterministic.</p>
     *
     * @param creature creature that will use the route
     * @param origin creature's current location
     * @param threatLocations locations occupied by known threats
     * @return the first exit on the escape route, or empty when none is safe
     */
    public Optional<Exit> findEscapeExit(
            Actor creature,
            Location origin,
            List<Location> threatLocations) {
        Queue<Location> pendingLocations = new ArrayDeque<>();
        Set<Location> visitedLocations = new HashSet<>();
        Map<Location, Integer> distanceByLocation = new HashMap<>();
        Map<Location, Exit> firstExitByLocation = new HashMap<>();

        pendingLocations.add(origin);
        visitedLocations.add(origin);
        distanceByLocation.put(origin, 0);

        Location bestLocation = null;

        while (!pendingLocations.isEmpty()) {
            Location currentLocation = pendingLocations.remove();

            for (Exit exit : sortedExits(currentLocation)) {
                Location nextLocation = exit.getDestination();

                if (visitedLocations.contains(nextLocation)
                        || !nextLocation.canActorEnter(creature)) {
                    continue;
                }

                visitedLocations.add(nextLocation);
                pendingLocations.add(nextLocation);
                distanceByLocation.put(
                        nextLocation,
                        distanceByLocation.get(currentLocation) + 1);

                Exit firstExit = currentLocation == origin
                        ? exit
                        : firstExitByLocation.get(currentLocation);
                firstExitByLocation.put(nextLocation, firstExit);

                if (safetyEvaluator.isSafe(nextLocation, threatLocations)
                        && isBetterLocation(
                        nextLocation,
                        bestLocation,
                        distanceByLocation,
                        threatLocations)) {
                    bestLocation = nextLocation;
                }
            }
        }

        return bestLocation == null
                ? Optional.empty()
                : Optional.of(firstExitByLocation.get(bestLocation));
    }

    /**
     * Determines whether one safe candidate ranks ahead of another.
     *
     * @param candidate newly found candidate
     * @param currentBest current best candidate, possibly null
     * @param distanceByLocation route distances from the origin
     * @param threatLocations locations occupied by known threats
     * @return true if the candidate should replace the current best location
     */
    private boolean isBetterLocation(
            Location candidate,
            Location currentBest,
            Map<Location, Integer> distanceByLocation,
            List<Location> threatLocations) {
        if (currentBest == null) {
            return true;
        }

        int candidateSafety = safetyEvaluator.nearestThreatDistance(
                candidate, threatLocations);
        int bestSafety = safetyEvaluator.nearestThreatDistance(
                currentBest, threatLocations);

        if (candidateSafety != bestSafety) {
            return candidateSafety > bestSafety;
        }

        int candidateDistance = distanceByLocation.get(candidate);
        int bestDistance = distanceByLocation.get(currentBest);

        if (candidateDistance != bestDistance) {
            return candidateDistance < bestDistance;
        }

        if (candidate.y() != currentBest.y()) {
            return candidate.y() < currentBest.y();
        }

        return candidate.x() < currentBest.x();
    }

    /**
     * Sorts exits to avoid dependence on collection insertion order.
     *
     * @param location location whose exits are being sorted
     * @return exits sorted by destination coordinates
     */
    private List<Exit> sortedExits(Location location) {
        List<Exit> exits = new ArrayList<>(location.getExits());
        exits.sort(Comparator
                .comparingInt((Exit exit) -> exit.getDestination().y())
                .thenComparingInt(exit -> exit.getDestination().x()));
        return exits;
    }
}
