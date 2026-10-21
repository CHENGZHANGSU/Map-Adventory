package game.products.animalproducts;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.SellAction;
import game.actors.Player;
import game.actors.Prop;
import game.interfaces.Sellable;
import game.interfaces.Tradable;

import java.util.Random;

/**
 * This class stores all properties and behaviors of Milk object.
 * It is a sub-class inherited from Item in engine package.
 *
 * @author Su Chengzhang
 */
public class Milk extends Item implements Sellable
{
    public Milk()
    {
        super("Milk", 'M');
        this.makePortable();
        this.addNewStatistic(Prop.COIN, new BaseStatistic(60));
    }

    /**
     * This method used to sell the milk from the farmer to the merchant.
     * @param seller The actor object sell the items to another actor.
     * @param other The actor object accept the selling.
     * @param gameMap   The game map as a GameMap object.
     * @return  The selling result as a string.
     */
    @Override
    public String soldBy(Actor seller, Actor other, GameMap gameMap)
    {
        String badNews = "❌Oops, the merchant pay nothing.😟";
        Random rand = new Random();

        // 50% to take the milk and pays nothing
        if (rand.nextDouble() > 0.5)
        {
            // deduct the payment back
            seller.modifyStatistic(Prop.COIN, StatisticOperations.DECREASE, this.getStatistic(Prop.COIN));
            return String.format("sell the %s successfully. %s",
                    this.toString().toLowerCase(), badNews);
        }
        return String.format("sell the %s successfully.", this.toString().toLowerCase());
    }

    /**
     * This method return the list of actions the otheractor can do with the milk.
     * @param otherActor the other actor
     * @param location the location of the other actor
     * @return  The ActionList object.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location)
    {
        ActionList actions = new ActionList();
        Tradable merchant = location.getActorAs(Tradable.class);
        Player player = location.getActorAs(Player.class);
        if (merchant != null)
        {
            actions.add(new SellAction(merchant, this));
        }
        if (player != null)
        {
            actions.add(this.getPickUpAction(otherActor));
        }
        return actions;
    }
}
