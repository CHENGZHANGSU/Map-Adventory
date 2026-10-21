package game.products.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.interfaces.ActionCapable;
import game.interfaces.Processable;


public abstract class ProcessedProduce extends Item implements Processable, ActionCapable
{
    private final Processable precedingProduct;

    /**
     * Creates a portable layer whose name prefixes the preceding product's complete name.
     *
     * @param nameModifier the prefix added by this processing method (e.g., "Preserved ")
     * @param precedingProduct the non-null complete product directly preceding this layer
     */
    public ProcessedProduce(String nameModifier, Processable precedingProduct) {
        super(
                nameModifier + precedingProduct.getItem().toString(),
                precedingProduct.getItem().getDisplayChar());
        this.precedingProduct = precedingProduct;
        this.makePortable();
    }

    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    public boolean requirementMet(Actor actor)
    {
        return actor.getInventory().getItems().contains(this);
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        actions.add(new ConsumeAction(this));
        return actions;
    }

    /**
     * Applies the preceding product's effects once before this layer's additional effect,
     * then removes this processed product from the actor's inventory.
     *
     * @param actor the actor consuming the complete product
     * @return descriptions of all effects in their execution order
     */
    @Override
    public String consumedBy(Actor actor, GameMap gameMap) {
        if (this.requirementMet(actor))
        {
            actor.getInventory().remove(this);

            return applyConsumptionEffects(actor, gameMap);
        }
        return String.format("Failed to consume %s, since it does not exist.\n", this.toString().toLowerCase());
    }

    /**
     * Applies the consumption effect specific to this processing method.
     *
     * @param actor the actor consuming the processed item
     * @return description of this layer's applied effect
     */
    protected abstract String applyProcessingEffect(Actor actor);

    @Override
    public String applyConsumptionEffects(Actor actor, GameMap map) {
        return precedingProduct.applyConsumptionEffects(actor, map)
                + "\n" + applyProcessingEffect(actor);
    }

    @Override
    public Item getItem() {
        return this;
    }
}
