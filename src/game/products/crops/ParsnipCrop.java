package game.products.crops;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actors.Prop;

/**
 * This class stores the entities and behaviors of the buyable parsnip crops.
 * It is a sub-class inherited BuyableCrop super class.
 *
 * @author Su Chengzhang
 */
public class ParsnipCrop extends BuyableCrop
{
    @Override
    public ParsnipCrop newPurchase() { return new ParsnipCrop(); }

    public ParsnipCrop()
    {
        super("Parsnip", 'p');
        this.addNewStatistic(Prop.COIN, new BaseStatistic(250));
    }

    @Override
    public Item droppedItem()
    {
        return new Parsnip("Parsnip", 'p');
    }

    @Override
    public Ground getGround()
    {
        return new game.terrains.ParsnipCrop();
    }
}
