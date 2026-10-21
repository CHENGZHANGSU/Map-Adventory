package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Consumable;

/**
 * This class store all properties and behaviours of a ConsumeAction object.
 * It is a sub-class inherited from Action class in engine package.
 *
 * @author Su Chengzhang
 */
public class ConsumeAction extends Action
{
    private final Consumable consumable;

    /**
     * This method uses to instantiate a ConsumeAction object.
     * @param consumable    Consumable item object.
     */
    public ConsumeAction(Consumable consumable)
    {
        this.consumable = consumable;
    }

    /**
     * This method used to execute the consume action of the consumable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of ConsumeAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.consumable.consumedBy(actor, map);
    }

    /**
     * This method used to describe the consume action of the consumable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the consume action of the consumable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s consume a %s",
                actor, this.consumable.toString().toLowerCase());
    }
}
