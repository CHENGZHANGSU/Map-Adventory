package game.products.minerals;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.SellAction;
import game.actors.Prop;
import game.interfaces.Sellable;
import game.interfaces.Tradable;

import java.util.Random;

/**
 * This class stores all the entities and behaviors of the Diamond object.
 * It is a sub-class inherit from Item super class, implement Sellable interface.
 *
 * @author Su Chengzhang
 */
public class Diamond extends Item implements Sellable
{
    public Diamond()
    {
            super("Diamond", '*');
            makePortable();
            this.addNewStatistic(Prop.COIN, new BaseStatistic(5000));
    }

    /**
     * This method used to sell the diamond from the farmer to the merchant.
     * @param seller The farmer object selling the diamond.
     * @param other The actor object accept the selling.
     * @param gameMap   The game map as a GameMap object.
     * @return  The selling result as a string.
     */
    @Override
    public String soldBy(Actor seller, Actor other, GameMap gameMap)
    {
        Random rand = new Random();

        // 50% to take the diamond and pays nothing
        if (rand.nextDouble() > 0.5)
        {
            if (!seller.getInventory().getItems().isEmpty())
            {
                // randomly pick an items from farmer's inventory
                Item item = seller.getInventory().getItems().get(rand.nextInt(seller.getInventory().getItems().size()));
                seller.getInventory().remove(item);

                String badNews = String.format("❌Oops, the merchant steal your %s.😟", item.toString().toLowerCase());
                return String.format("sell the %s successfully. %s",
                        this.toString().toLowerCase(), badNews);
            }
            else
            {
                String badNews = String.format("❌Oops, has nothing to steal.🤯");
                return String.format("sell the %s successfully. %s",
                        this.toString().toLowerCase(), badNews);
            }
        }
        return String.format("sell the %s successfully.", this.toString().toLowerCase());
    }

    /**
     * This method return the list of actions the otheractor can do with the diamond.
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
