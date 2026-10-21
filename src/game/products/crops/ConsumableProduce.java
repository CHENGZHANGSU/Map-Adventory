package game.products.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.interfaces.ActionCapable;
import game.interfaces.Consumable;
import game.interfaces.Processable;

/** Portable produce that can only be consumed from its owner's inventory. */
public abstract class ConsumableProduce extends Item implements Consumable, ActionCapable
{
    /**
     * Creates portable, edible produce.
     * @param name item name shown in menus
     * @param symbol item symbol on the ground
     */
    protected ConsumableProduce(String name, char symbol) {
        super(name, symbol);
        makePortable();
    }

    @Override
    public boolean requirementMet(Actor actor) {
        return actor.getInventory().getItems().contains(this);
    }

    @Override
    public final String consumedBy(Actor actor, GameMap map) {
        if (!requirementMet(actor)) {
            return actor + " cannot consume " + this + ": it is not in the inventory.";
        }
        actor.getInventory().remove(this);
        return consume(actor);
    }

    /**
     * Applies food-specific effects after the original item has been removed.
     * @param actor actor consuming the food
     * @return description of the food's effects
     */
    protected abstract String consume(Actor actor);

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return new ActionList(new ConsumeAction(this));
    }
}
