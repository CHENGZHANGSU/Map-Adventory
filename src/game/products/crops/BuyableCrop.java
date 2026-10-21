package game.products.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Prop;
import game.interfaces.Buyable;
import game.terrains.Crop;
import game.terrains.Dirt;
import game.terrains.Soil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This class stores the entities and behaviors of a buyable crops.
 * It is a sub-class inherited from Item super class, implement Buyable interface.
 * It is an abstract class.
 *
 * @author Su Chengzhang
 */
public abstract class BuyableCrop extends Item implements Buyable
{
    public final Random rand = new Random();

    public BuyableCrop(String name, Character displayChar)
    {
        super(name, displayChar);
    }

    /**
     * This method used to buy the items from the merchant.
     * @param buyer The actor object buy the items from another actor.
     * @param other The actor object accept the buying.
     * @param gameMap   The game map as a GameMap object.
     * @return  The buying result as a string.
     */
    public String boughtBy(Actor buyer, Actor other, GameMap gameMap)
    {
        Random rand = new Random();

        List<Exit> exits = new ArrayList<>(gameMap.locationOf(other).getExits());

        if (!exits.isEmpty())
        {
                exits = this.findExit(exits);

            if (!exits.isEmpty())
            {
                // randomly pick a tile adjacent to the merchant
                Exit plantExit = exits.get(rand.nextInt(exits.size()));
                plantExit.getDestination().setGround(this.getGround());

                // get the surrounding of the crop
                exits = plantExit.getDestination().getExits();
                // randomly drop three parsnips at the surrounding of the crop
                for (int i = 0; i <= 2; i++)
                {
                    Exit dropExit = exits.get(rand.nextInt(exits.size()));
                    dropExit.getDestination().addItem(this.droppedItem());
                }

                String additionalEffect = this.additionalEffect(buyer);
                if (additionalEffect != null)
                {
                    return String.format("purchase the %s successfully. %s",
                            this.toString().toLowerCase(), additionalEffect);
                }

                return String.format("purchase the %s successfully.",
                        this.toString().toLowerCase());
            }
        }
        // refund to the farmer
        buyer.modifyStatistic(Prop.COIN, StatisticOperations.INCREASE, this.getStatistic(Prop.COIN));
        return String.format("fail to purchase the %s due to insufficient spaces.", this.toString().toLowerCase());
    }

    /**
     * This method used to find the exit that without a planted crop.
     * @param exits The Exit object.
     * @return  The list of Exit object available for planting a new crop.
     */
    protected List<Exit> findExit(List<Exit> exits)
    {
        for (int i = 0; i < exits.size(); i++)
        {
            Exit exit = exits.get(i);
            // the crop exist in this location
            if (exit.getDestination().getGroundAs(Crop.class) != null)
            {
                exits.remove(exit);
            }
            // not either soil or dirt
            else if (!(Objects.equals(exit.getDestination().getGround(), new Dirt()) ||
                    Objects.equals(exit.getDestination().getGround(), new Soil())))
            {
                exits.remove(exit);
            }
        }
        return exits;
    }

    /**
     * This method used to handle the dropped items when a crop is planted.
     * @return  The Item object need to be dropped.
     */
    public abstract Item droppedItem();

    /**
     * This method used to handle the specified crop planted by the merchant as Ground object.
     * @return  The planted crop as Ground object.
     */
    public abstract Ground getGround();

    /**
     * This method used to handle the additional effect of the planted crops after buying by the farmer.
     * @param buyer The Actor object as a buyer.
     * @return  The additional effects as a String, default null.
     */
    public String additionalEffect(Actor buyer)
    {
        return null;
    }
}
