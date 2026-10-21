package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This interface stores the methods the buyable objects can perform.
 *
 * @author Su Chengzhang
 */
public interface Buyable
{
    /** Creates an independent product for one transaction; stock is a catalogue entry. */
    Buyable newPurchase();

    /**
     * This method used to buy the items from the merchant.
     * @param buyer The actor object buy the items from another actor.
     * @param other The actor object accept the buying.
     * @param gameMap   The game map as a GameMap object.
     * @return  The buying result as a string.
     */
    String boughtBy(Actor buyer, Actor other, GameMap gameMap);
}
