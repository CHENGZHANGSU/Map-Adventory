package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Waterable;

/**
 * This class store all properties and behaviours of a WaterAction object.
 * It is a sub-class inherited from Action class in engine package.
 *
 * @author Su Chengzhang
 */
public class WaterAction extends Action
{
    private final Waterable waterable;
    private final String direction;

    /**
     * This parameterized constructor is used to instantiate a WaterAction object.
     * @param waterable  A waterable item.
     */
    public WaterAction(Waterable waterable)
    {
        this(waterable, "");
    }

    /**
     * Creates an action whose menu identifies the target and its direction.
     * @param waterable target to water
     * @param direction target direction, or an empty string for the actor's own tile
     */
    public WaterAction(Waterable waterable, String direction)
    {
        this.waterable = waterable;
        this.direction = direction;
    }

    /**
     * This method used to execute the water action of the waterable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of WaterAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.waterable.wateredBy(actor, map);
    }

    /**
     * This method used to describe the water action of the waterable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the water action of the waterable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " water " + waterable
                + (direction.isEmpty() ? "" : " at " + direction);
    }
}
