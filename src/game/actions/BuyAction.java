package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Buyable;
import game.interfaces.Tradable;

/**
 * This class stores all entities and behaviors needed for buy action to be performed.
 * It is a sub-class inherited from Action super class.
 *
 * @author Su Chengzhang
 */
public class BuyAction extends Action
{
    private final Tradable trader;
    private final Buyable item;

    public BuyAction(Tradable trader, Buyable item)
    {
        this.trader = trader;
        this.item = item;
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
        return this.trader.tradeBy(actor, map, this.item);
    }

    /**
     * This method used to describe the buy action of the buyable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the buy action of the buyable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s buy a %s from %s",
                actor, this.item.toString().toLowerCase(), this.trader);
    }
}
