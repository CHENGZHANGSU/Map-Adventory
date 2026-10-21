package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * This interface contains all the capable actions an object can take.
 *
 * @author Su Chengzhang
 */
public interface ActionCapable
{
    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    boolean requirementMet(Actor actor);
}
