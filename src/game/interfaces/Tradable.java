package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This interface stores the methods the consumable objects can perform.
 * It is a sub-interface inherited from ActionCapable.
 *
 * @author Su Chengzhang
 */
public interface Tradable extends ActionCapable
{
    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    boolean requirementMet(Actor actor);

    /**
     * This method used to trade the items between the merchant and farmer.
     * @param actor The farmer or merchant as an Actor object.
     * @param gameMap   The game map as a GameMap object.
     * @param item The item involved in the trade.
     * @return  The result of trading as a string.
     */
    String tradeBy(Actor actor, GameMap gameMap, Sellable item);

    /**
     * This method used to trade the items between the merchant and farmer.
     * @param actor The farmer or merchant as an Actor object.
     * @param gameMap   The game map as a GameMap object.
     * @param item The item involved in the trade.
     * @return  The result of trading as a string.
     */
    String tradeBy(Actor actor, GameMap gameMap, game.interfaces.Buyable item);


}
