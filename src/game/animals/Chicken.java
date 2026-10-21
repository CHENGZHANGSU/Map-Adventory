package game.animals;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Consumable;
import game.interfaces.Feedable;
import game.interfaces.Wanderable;
import game.products.animalproducts.Egg;
import jdk.jfr.Percentage;

import java.util.List;
import java.util.Random;

/**
 * This class store all properties and behaviours of a Chicken object.
 * It is a sub-class inherited from Actor class in engine package.
 *
 * @author Su Chengzhang
 */
public class Chicken extends Animal implements Feedable
{

    public Chicken()
    {
        super("Chicken", 'c', 10);
        this.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 5);
        this.getInventory().add(new Egg());
    }

    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    @Override
    public boolean requirementMet(Actor actor)
    {
        return actor.isConscious();
    }

    /**
     * This method used to feed a feedable item by the actor on the game map.
     * @param otherActor Feeder as an Actor object.
     * @param location   The location of the other actor.
     * @return  The result of the feed action by the actor as a string.
     */
    @Override
    public String fedBy(Actor otherActor, Location location, Consumable food)
    {
        String action = String.format("feed a %s", this.toString().toLowerCase());
        if (this.requirementMet(this))
        {
            // remove from farmer's inventory
            otherActor.getInventory().remove((Item) food);

            // restore HPs
            this.heal(3);

            // drop 2 stamina
            otherActor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, 2);
            int previousSP = otherActor.getStatistic(ActorStatistics.STAMINA);

            // Increase the maximum stamina by 1
            otherActor.modifyStatisticMaximum(ActorStatistics.STAMINA, StatisticOperations.INCREASE, 1);
            otherActor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.UPDATE, previousSP);

            String remainStamina = String.format("(stamina: %d/%d)",
                    otherActor.getStatistic(ActorStatistics.STAMINA), otherActor.getMaximumStatistic(ActorStatistics.STAMINA));

            String res = String.format("%s %s successfully %s.", otherActor, action, remainStamina);

            // randomly drop products
            List<Exit> exit = location.getExits();
            if (!exit.isEmpty())
            {
                Exit exit1 = exit.get(super.rand.nextInt(exit.size()));
                exit1.getDestination().addItem(new Egg());
                if (super.rand.nextDouble() > 0.5)
                {
                    // 50% drop another egg.
                    exit1 = exit.get(super.rand.nextInt(exit.size()));
                    exit1.getDestination().addItem(new Egg());
                    res += "\nNew eggs dropped!";
                }
            }
            else
            {
                res += "\nThere is no place for eggs.";
            }
            return res;
        }
        else
        {
            return String.format("%s failed due to insufficient HP of the animal or insufficient SP of the %s.%n",
                    action, otherActor);
        }
    }
}
