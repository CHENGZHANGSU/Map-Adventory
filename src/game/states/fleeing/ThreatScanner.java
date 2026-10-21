package game.states.fleeing;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Player;
import game.interfaces.Hostile;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds actors considered dangerous by a fleeing SporeBeast.
 */
public class ThreatScanner {

    /**
     * Finds nearby players and hostile creatures, excluding the SporeBeast.
     *
     * @param creature the actor scanning for danger
     * @param origin the scanning actor's location
     * @param radius the square search radius
     * @return detected threats
     */
    public List<Actor> findNearbyThreats(
            Actor creature,
            Location origin,
            int radius) {
        List<Actor> threats = new ArrayList<>();

        for (Location location : origin.getNearbyLocations(radius)) {
            if (!location.containsAnActor()) {
                continue;
            }

            Actor actor = location.getActor();
            if (actor != creature && isThreat(actor)) {
                threats.add(actor);
            }
        }

        return threats;
    }

    /**
     * Checks whether a danger occupies an adjacent location.
     *
     * @param creature the actor scanning for danger
     * @param origin the scanning actor's location
     * @return true if a threat is adjacent
     */
    public boolean hasAdjacentThreat(Actor creature, Location origin) {
        return !findNearbyThreats(creature, origin, 1).isEmpty();
    }

    /**
     * Identifies the kinds of actor considered a danger to the SporeBeast.
     *
     * @param actor actor being examined
     * @return true if the actor is dangerous
     */
    private boolean isThreat(Actor actor) {
        return actor instanceof Player || actor instanceof Hostile;
    }
}
