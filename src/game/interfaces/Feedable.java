package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * This interface stores the methods the feedable objects can perform.
 * It is a sub-interface inherited from ActionCapable.
 *
 * @author Su Chengzhang
 */
public interface Feedable extends ActionCapable
{
    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    boolean requirementMet(Actor actor);


    /**
     * This method used to feed a feedable item by the actor on the game map.
     * @param otherActor The actor fed by the farmer.
     * @param location   The location of the other actor.
     * @param food The Consumable Item can be fed.
     * @return  The result of the feed action by the actor as a string.
     */
    String fedBy(Actor otherActor, Location location, Consumable food);

}
