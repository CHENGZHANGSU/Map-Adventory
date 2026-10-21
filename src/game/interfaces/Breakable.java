package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Contract for entities that can be broken by a tool, supporting the Open-Closed Principle.
 *
 * @author Zizhuo Wang
 */
public interface Breakable {
    /**
     * Executes the breaking mechanic for the entity.
     *
     * @param actor    The actor breaking the entity.
     * @param location The location of the breakable entity.
     * @param map      The current game map.
     * @return A string describing the outcome.
     */
    String breakEntity(Actor actor, Location location, GameMap map);
}