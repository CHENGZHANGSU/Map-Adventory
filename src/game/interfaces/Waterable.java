package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This interface stores the methods the waterable objects can perform.
 * It is a sub-interface inherited from ActionCapable.
 *
 * @author Su Chengzhang
 */
public interface Waterable extends ActionCapable
{
    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    boolean requirementMet(Actor actor);

    /**
     * This method used to water the dirt by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the water action by the actor as a string.
     */
    String wateredBy(Actor actor, GameMap gameMap);
}
