package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Sellable;
import game.interfaces.Tradable;

/**
 * This class stores all entities and behaviors needed for sell action to be performed.
 * It is a sub-class inherited from Action super class.
 *
 * @author Su Chengzhang
 */
public class SellAction extends Action
{
    private final Tradable merchant;
    private final Sellable item;

    public SellAction(Tradable merchant, Sellable item)
    {
        this.merchant = merchant;
        this.item = item;
    }

    /**
     * This method used to execute the sell action of the sellable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of SellAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.merchant.tradeBy(actor, map, this.item);
    }

    /**
     * This method used to describe the sell action of the sellable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the sell action of the sellable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s sell a %s to %s",
                actor, this.item.toString().toLowerCase(), this.merchant);
    }
}
