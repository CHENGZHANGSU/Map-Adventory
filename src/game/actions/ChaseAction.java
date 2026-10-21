package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Chaseable;

public class ChaseAction extends Action
{
    private final Chaseable chaseable;
    private final Location targetLocation;

    public ChaseAction(Chaseable chaseable, Location targetLocation)
    {
        this.chaseable = chaseable;
        this.targetLocation = targetLocation;
    }

    /**
     * This method used to execute the buy action of the buyable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of BuyAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.chaseable.chasedBy(actor, map, targetLocation);
    }

    /**
     * This method used to describe the buy action of the buyable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the buy action of the buyable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s is chasing the %s",
                this.chaseable.toString(), actor);
    }
}
