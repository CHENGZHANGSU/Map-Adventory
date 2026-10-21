package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Breakable;

/**
 * Action allowing an actor to break a Breakable target.
 * @author Zizhuo Wang
 */
public class BreakAction extends Action {
    private final Breakable target;
    private final Location location;

    public BreakAction(Breakable target, Location location) {
        this.target = target;
        this.location = location;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return target.breakEntity(actor, location, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks the target";
    }
}