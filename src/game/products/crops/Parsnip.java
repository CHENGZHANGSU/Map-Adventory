package game.products.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.actions.FeedAction;
import game.interfaces.*;


/**
 * This class stores all properties and behaviors of Parsnip object.
 * It is a sub-class inherited from Item in engine package, implemented Consumable interface.
 *
 * @author Su Chengzhang
 */
public class Parsnip extends Item implements Consumable, Processable
{

    public Parsnip(String name, char displayChar)
    {
        super(name, displayChar);
        this.makePortable();
    }


    /**
     * This method used to consume a parsnip by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the consume action by the actor as a string.
     */
    @Override
    public String consumedBy(Actor actor, GameMap gameMap)
    {
        if (!actor.getInventory().getItems().contains(this))
            return actor + " cannot consume " + this + ": not in inventory.";
        actor.getInventory().remove(this);
        return applyConsumptionEffects(actor, gameMap);
    }

    @Override
    public String applyConsumptionEffects(Actor actor, GameMap gameMap) {
        String action = "consume a raw parsnip";
        // default heal point
        int healPoint = 1;

        actor.heal(healPoint);

        String remainStamina = String.format("(stamina: %d/%d)",
                actor.getStatistic(ActorStatistics.STAMINA), actor.getMaximumStatistic(ActorStatistics.STAMINA));
        return String.format("%s %s successfully %s.",
                actor, action, remainStamina);
        }

    /**
     * This method return the list of actions the otheractor can do with parsnip.
     * @param otherActor the other actor
     * @param location the location of the other actor
     * @return  The ActionList object.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location)
    {
        ActionList actions = new ActionList();
        Feedable animal = location.getActorAs(Feedable.class);
        if (animal != null)
        {
            actions.add(new FeedAction(this, animal));
        }
        return super.allowableActions(otherActor, location);
    }

    /**
     * This method return the list of actions the farmer can do with parsnip.
     * @param owner the actor that owns the item
     * @param map the map where the actor is performing the action on
     * @return  The ActionList object.
     */
    public ActionList allowableActions(Actor owner, GameMap map)
    {
        return new ActionList(new ConsumeAction(this));
    }

    @Override
    public Item getItem() {
        return this;
    }
}
