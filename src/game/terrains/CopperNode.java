package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakAction;
import game.interfaces.Breakable;
import game.interfaces.Breaker;
import game.interfaces.Chargeable;
import game.products.minerals.CopperOre;

/**
 * An impassable copper node that can be broken using a suitable breaking tool.
 * Breaking a copper node replaces it with dirt, drops copper ore, and
 * charges every adjacent ground tile capable of holding a charge.
 *
 * @author Zizhuo Wang
 */
public class CopperNode extends Ground implements Breakable {

    private static final String COPPER_NODE_NAME = "Copper Node";

    /**
     * Creates a copper node.
     */
    public CopperNode() {
        super('N', COPPER_NODE_NAME);
    }

    /**
     * Prevents actors from entering the copper node's location.
     *
     * @param actor actor attempting to enter
     * @return false because copper nodes are impassable
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Provides a break action when the actor has a suitable breaking tool.
     *
     * @param actor actor attempting to interact with the copper node
     * @param location location containing the copper node
     * @param direction direction of the node from the actor
     * @return available actions for the actor
     */
    @Override
    public ActionList allowableActions(
            Actor actor,
            Location location,
            String direction) {

        ActionList actions =
                super.allowableActions(actor, location, direction);

        if (hasBreakingTool(actor)) {
            actions.add(new BreakAction(this, location));
        }

        return actions;
    }

    /**
     * Breaks the copper node, replaces it with dirt, drops copper ore, and
     * charges all adjacent chargeable ground.
     *
     * @param actor actor breaking the copper node
     * @param location location containing the copper node
     * @param map current game map
     * @return description of the breaking result
     */
    @Override
    public String breakEntity(
            Actor actor,
            Location location,
            GameMap map) {

        if (!hasBreakingTool(actor)) {
            return actor
                    + " cannot break the copper node without a suitable tool.";
        }

        if (location.getGround() != this) {
            return "The copper node is no longer at this location.";
        }

        location.setGround(new Dirt());
        location.addItem(new CopperOre());

        int chargedGroundCount = chargeAdjacentGround(location);

        return actor + " broke the copper node and uncovered copper ore. "
                + chargedGroundCount
                + " adjacent chargeable ground tile(s) became electrified.";
    }

    /**
     * Checks whether the actor carries a tool capable of breaking entities.
     *
     * @param actor actor whose inventory is checked
     * @return true if the actor has a breaking tool
     */
    private boolean hasBreakingTool(Actor actor) {
        for (Item item : actor.getInventory().getItems()) {
            if (item instanceof Breaker) {
                return true;
            }
        }

        return false;
    }

    /**
     * Charges every adjacent ground tile that implements {@link Chargeable}.
     *
     * @param location location of the broken copper node
     * @return number of charged ground tiles
     */
    private int chargeAdjacentGround(Location location) {
        int chargedGroundCount = 0;

        for (Exit exit : location.getExits()) {
            Ground adjacentGround = exit.getDestination().getGround();

            if (adjacentGround instanceof Chargeable) {
                Chargeable chargeableGround = (Chargeable) adjacentGround;
                chargeableGround.setCharged(true);
                chargedGroundCount++;
            }
        }

        return chargedGroundCount;
    }
}
