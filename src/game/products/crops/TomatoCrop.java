package game.products.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Prop;

import java.util.Random;

/**
 * This class stores the entities and behaviors of the buyable tomato crops.
 * It is a sub-class inherited BuyableCrop super class.
 *
 * @author Su Chengzhang
 */
public class TomatoCrop extends BuyableCrop
{
    @Override
    public TomatoCrop newPurchase() { return new TomatoCrop(); }

    public TomatoCrop()
    {
        super("Tomato", 'o');
        this.addNewStatistic(Prop.COIN, new BaseStatistic(500));
    }

    @Override
    public Item droppedItem()
    {
        return new Tomato("Tomato", 'o');
    }

    @Override
    public Ground getGround()
    {
        return new game.terrains.TomatoCrop();
    }

    @Override
    public String additionalEffect(Actor buyer)
    {
        Random rand = new Random();
        String goodNews = "✔Great, the merchant takes no payment.😊";

        if (rand.nextDouble() > 0.5)
        {
            buyer.modifyStatistic(Prop.COIN, StatisticOperations.INCREASE, this.getStatistic(Prop.COIN));
            return goodNews;
        }
        return null;
    }
}
