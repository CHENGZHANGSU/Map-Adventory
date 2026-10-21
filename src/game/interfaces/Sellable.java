package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This interface stores the methods the sellable objects can perform.
 * It is a sub-interface inherited from ActionCapable.
 *
 * @author Su Chengzhang
 */
public interface Sellable
{
    /**
     * This method used to sell the items from the farmer to the merchant.
     * @param seller The actor object sell the items to another actor.
     * @param other The actor object accept the selling.
     * @param gameMap   The game map as a GameMap object.
     * @return  The selling result as a string.
     */
    String soldBy(Actor seller, Actor other, GameMap gameMap);
}
