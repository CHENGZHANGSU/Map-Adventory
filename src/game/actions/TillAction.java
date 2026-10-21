package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Tillable;

/**
 * This class store all properties and behaviours of a TillAction object.
 * It is a sub-class inherited from Action class in engine package.
 *
 * @author Su Chengzhang
 */
public class TillAction extends Action
{
    private final Tillable tillable;

    /**
     * This parameterized constructor is used to instantiate a TillAction object.
     * @param tillable  A tillable item.
     */
    public TillAction(Tillable tillable)
    {
        this.tillable = tillable;
    }

    /**
     * This method used to execute the till action of the tillable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of TillAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.tillable.tiltBy(actor, map);
    }

    /**
     * This method used to describe the till action of the tillable item in menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the till action of the tillable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return actor.toString() + " till the tile.";
    }
}
