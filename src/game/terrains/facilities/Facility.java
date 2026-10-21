package game.terrains.facilities;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ProcessAction;
import game.interfaces.Processable;
import game.products.crops.ProcessedProduce;

public abstract class Facility extends Ground
{
    public Facility(char displayChar, String name) {
        super(displayChar, name);
    }
    /**
     * Factory method: wraps the given processable product into this facility's corresponding
     * decorator.
     *
     * @param product the product before processing
     * @return a new ProcessedProduct decorating the input product
     */
    public abstract ProcessedProduce process(Processable product);

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        for (Processable item : actor.getInventory().getItemsAs(Processable.class)) {
            actions.add(new ProcessAction(item, this, direction));
        }
        return actions;
    }
}
