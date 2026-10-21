package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Teleportable;

/**
 * Action allowing an actor to use a Teleportable mechanism.
 *
 * @author Zizhuo Wang
 */
public class TeleportAction extends Action {
    private final Teleportable mechanism;
    private final Location destination;
    private final String mechanismName;

    public TeleportAction(Teleportable mechanism, Location destination, String mechanismName) {
        this.mechanism = mechanism;
        this.destination = destination;
        this.mechanismName = mechanismName;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return mechanism.teleport(actor, destination, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses " + mechanismName + " to teleport";
    }
}