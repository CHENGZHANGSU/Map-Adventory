package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Contract for mechanisms that can teleport an actor.
 *
 * @author Zizhuo Wang
 */
public interface Teleportable {
    /**
     * Teleports the actor to a destination.
     *
     * @param actor       The actor to teleport.
     * @param destination The target location.
     * @param map         The current game map.
     * @return A string describing the teleportation outcome.
     */
    String teleport(Actor actor, Location destination, GameMap map);
}