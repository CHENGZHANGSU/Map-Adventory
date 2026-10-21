package game.interfaces;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * This interface stores the methods the wandering action performed by an actor.
 * @author Su Chengzhang
 */
public interface Wanderable
{
    /**
     * This method uses to perform the wandering action of the actor.
     * @param actor Actor object wandering.
     * @param location Location of the actor.
     */
    Action wander(Actor actor, Location location);
}
