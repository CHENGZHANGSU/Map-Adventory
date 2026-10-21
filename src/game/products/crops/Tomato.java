package game.products.crops;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.actions.FeedAction;
import game.actions.ProcessAction;
import game.actors.Prop;
import game.interfaces.*;
import game.products.Status;
import game.terrains.Dirt;
import game.terrains.Soil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * This class stores all properties and behaviors of Tomato object.
 * It is a sub-class inherited from Item in engine package, implemented Consumable interface.
 *
 * @author Su Chengzhang
 */
public class Tomato extends Item implements Consumable, Processable
{
    public Tomato(String name, char displayChar)
    {
        super(name, displayChar);
        this.makePortable();

    }

    /**
     * This method used to determine whether the actor can perform actions on Tomato object.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    public boolean requirementMet(Actor actor)
    {
        return actor.getInventory().getItems().contains(this);
    }

    /**
     * This method used to consume a tomato by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the consume action by the actor as a string.
     */
    @Override
    public String consumedBy(Actor actor, GameMap gameMap)
    {
        if (!requirementMet(actor)) return actor + " cannot consume " + this + ": not in inventory.";
        actor.getInventory().remove(this);
        return applyConsumptionEffects(actor, gameMap);
    }

    @Override
    public String applyConsumptionEffects(Actor actor, GameMap gameMap) {
        String action = "consume a raw tomato";
        // default heal point
        int staminaPoint = 1;

        // each preservation layer increase 1 SP.
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.INCREASE, staminaPoint);

        // maximum HP increase by 2
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.UPDATE,
                actor.getMaximumStatistic(ActorStatistics.HEALTH) + 2);

        String remainStamina = String.format("(stamina: %d/%d)",
                actor.getStatistic(ActorStatistics.STAMINA), actor.getMaximumStatistic(ActorStatistics.STAMINA));
        return String.format("%s %s successfully %s.",
                actor, action, remainStamina);
    }


    /**
     * This method return the list of actions the otheractor can do with tomato.
     * @param otherActor the other actor
     * @param location the location of the other actor
     * @return  The ActionList object.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location)
    {
        Feedable animal = location.getActorAs(Feedable.class);
        if (animal != null)
        {
            return new ActionList(new FeedAction(this, animal));
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
