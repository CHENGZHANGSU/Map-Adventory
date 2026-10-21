package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Coatable;
import game.products.weapons.WeaponCoating;

/**
 * An action allowing an actor to apply a coating to a coatable weapon.
 * Can optionally consume an item from the actor's inventory (e.g., a Melon).
 */
public class CoatWeaponAction extends Action {

    private final Coatable weapon;
    private final WeaponCoating coating;
    private final Item consumedSource;

    /**
     * Constructor for CoatWeaponAction.
     *
     * @param weapon         the coatable weapon to be enchanted
     * @param coating        the coating strategy to apply
     * @param consumedSource the item to consume from inventory, or null if environmental
     */
    public CoatWeaponAction(Coatable weapon, WeaponCoating coating, Item consumedSource) {
        this.weapon = weapon;
        this.coating = coating;
        this.consumedSource = consumedSource;
    }

    /**
     * Executes the coating application.
     * Replaces any existing coating on the weapon and consumes the item if required.
     *
     * @param actor The actor performing the action.
     * @param map   The map the actor is on.
     * @return a string describing the successful coating
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        this.weapon.setCoating(this.coating);

        String consumeMsg = "";
        if (this.consumedSource != null) {
            actor.getInventory().remove(this.consumedSource);
            consumeMsg = " (consumed " + this.consumedSource.toString() + ")";
        }

        return String.format("%s successfully coats the %s with %s coating%s.",
                actor, this.weapon.toString(), this.coating.getName(), consumeMsg);
    }

    /**
     * Describes the action in the UI menu.
     *
     * @param actor The actor performing the action.
     * @return a String like "Farmer coats Sword with Fire coating"
     */
    @Override
    public String menuDescription(Actor actor) {
        return String.format("%s coats %s with %s coating",
                actor, this.weapon.toString(), this.coating.getName());
    }
}
