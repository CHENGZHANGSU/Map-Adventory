package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.CoatWeaponAction;
import game.interfaces.Chargeable;
import game.interfaces.Coatable;
import game.products.weapons.ChargedCoating;

/**
 * An abstract base class for all ground types that can hold an electrical charge.
 * It handles the charged state and automatically provides coating actions
 * for any coatable weapon in the inventory of an actor interacting with it.
 */
public abstract class ChargeableGround extends Ground implements Chargeable {

    private boolean charged;

    /**
     * Constructor.
     *
     * @param displayChar character to display for this type of terrain
     * @param name name of this type of terrain
     */
    public ChargeableGround(char displayChar, String name) {
        super(displayChar, name);
        this.charged = false;
    }

    @Override
    public void setCharged(boolean charged) {
        this.charged = charged;
    }

    @Override
    public boolean isCharged() {
        return this.charged;
    }

    /**
     * Generates allowable actions for an actor standing on or adjacent to this ground.
     * If the ground is charged, it scans the actor's inventory and provides a coating action.
     *
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return an ActionList containing available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (isCharged()) {
            for (Item item : actor.getInventory().getItems()) {
                if (item instanceof Coatable) {
                    actions.add(new CoatWeaponAction((Coatable) item, new ChargedCoating(), null));
                }
            }
        }

        return actions;
    }
}
