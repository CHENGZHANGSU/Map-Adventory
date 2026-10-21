package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.products.trap.Trap;

/**
 * This class stores all entities and behaviors needed for discard action to be performed.
 * It is a sub-class inherited from Action super class.
 *
 * @author Su Chengzhang
 */
public class DiscardAction extends Action
{
    private final Trap trap;

    /**
     * This method uses to instantiate a DiscardAction object
     * @param trap  The Trap object.
     */
    public DiscardAction(Trap trap)
    {
        this.trap = trap;
    }

    /**
     * This method used to execute the discard action of the discardable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of DiscardAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.trap.discardedBy(actor, map);
    }

    /**
     * This method used to describe the discard action of the discardable object in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the discard action of the discardable object as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s discard the %s",
                actor, this.trap.toString());
    }
}
