package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * This interface stores the methods the consumable objects can perform.
 *
 * @author Su Chengzhang
 */
public interface Consumable
{
    /**
     * This method used to consume a consumable item by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the consume action by the actor as a string.
     */
    String consumedBy(Actor actor, GameMap gameMap);
}
