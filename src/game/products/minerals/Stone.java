package game.products.minerals;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.SellAction;
import game.actors.Prop;
import game.interfaces.Sellable;
import game.interfaces.Tradable;
import game.terrains.Rock;

import java.util.Random;

/**
 * This class stores all the entities and behaviors of the Stone object.
 * It is a sub-class inherit from Item super class, implement Sellable interface.
 *
 * @author Su Chengzhang
 */
public class Stone extends Item implements Sellable
{
    public Stone()
    {
        super("Stone", 'S');
        makePortable();
        this.addNewStatistic(Prop.COIN, new BaseStatistic(10));
    }

    /**
     * This method used to sell the stone from the farmer to the merchant.
     * @param seller The actor object sell the items to another actor.
     * @param other The actor object accept the selling.
     * @param gameMap   The game map as a GameMap object.
     * @return  The selling result as a string.
     */
    public String soldBy(Actor seller, Actor other, GameMap gameMap)
    {
        Random rand = new Random();

        Location actorLocate = gameMap.locationOf(other);
        // Build a rock on random adjacent tile by the merchant
        Exit exit = actorLocate.getExits().get(rand.nextInt(actorLocate.getExits().size()));
        exit.getDestination().setGround(new Rock());
        return String.format("sell the %s successfully.", this.toString().toLowerCase());

    }

    /**
     * This method return the list of actions the otheractor can do with the stone.
     * @param otherActor the other actor
     * @param location the location of the other actor
     * @return  The ActionList object.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location)
    {
        Tradable merchant = location.getActorAs(Tradable.class);
        if (merchant != null)
        {
            return new ActionList(new SellAction(merchant, this));
        }
        return super.allowableActions(otherActor, location);
    }
}
