package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Consumable;
import game.interfaces.Feedable;

/**
 * This class store all properties and behaviours of a FeedAction object.
 * It is a sub-class inherited from Action class in engine package.
 *
 * @author Su Chengzhang
 */
public class FeedAction extends Action
{
    private final Consumable food;
    private final Feedable otherActor;

    /**
     * This method uses to instantiate a FeedAction object.
     * @param food   Consumable item objet.
     * @param otherActor     Other actor object.
     */
    public FeedAction(Consumable food, Feedable otherActor)
    {
        this.food = food;
        this.otherActor =  otherActor;
    }

    /**
     * This method used to execute the feed action of the consumable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of FeedAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.otherActor.fedBy(actor, map.locationOf(actor), this.food);
    }

    /**
     * This method used to describe the feed action of the feedable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the feed action of the feedable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s feed a %s uses a %s.",
                actor, this.otherActor, this.food.toString().toLowerCase());
    }
}

