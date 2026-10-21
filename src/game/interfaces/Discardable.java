package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Discardable
{
    /**
     * This method used to discard a discardable item by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the consume action by the actor as a string.
     */
    String discardedBy(Actor actor, GameMap gameMap);
}
