package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public interface Chaseable
{
    /**
     * This method used to chase an actor by the chasable actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @param targetLocation The location of the target.
     * @return  The result of the chase action by the actor as a string.
     */
    String chasedBy(Actor actor, GameMap gameMap, Location targetLocation);
}
