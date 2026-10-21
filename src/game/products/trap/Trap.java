package game.products.trap;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.DiscardAction;
import game.actors.Player;
import game.interfaces.Discardable;
import game.tools.Pickaxe;
import game.tools.ToolAbility;

/**
 * This class stores all entities and behaviors of the Trap object.
 * It is an abstract class inherit from Item class, implement Discardable interface.
 *
 * @author Su Chengzhang
 */
public abstract class Trap extends Item implements Discardable
{
    public Trap(String name, Character displayChar)
    {
        super(name, displayChar);
    }

    /**
     * This method used to discard a discardable item by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the consume action by the actor as a string.
     */
    @Override
    public String discardedBy(Actor actor, GameMap gameMap)
    {
        Location location = gameMap.locationOf(actor);
        Player player = location.getActorAs(Player.class);
        if (player != null && player.hasAbility(ToolAbility.DISCARD))
        {
            // remove the trap
            location.removeItem(this);
            return String.format("The %s discard by %s successfully.", this, player);
        }
        return this.extraEffect(player);
    }

    public abstract String extraEffect(Player player);

    /**
     * List of allowable actions that can be performed on the item when it is on the ground
     * Example #1: a trap can return an action to disarm the trap.
     *
     * @param location the location of the ground on which the item lies
     * @return an unmodifiable list of Actions
     */
    public ActionList allowableActions(Location location)
    {
        Player player = location.getActorAs(Player.class);
        if (player != null)
        {
            return new ActionList(new DiscardAction(this));
        }
        return new ActionList();
    }
}
