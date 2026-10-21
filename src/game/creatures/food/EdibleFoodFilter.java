package game.creatures.food;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Consumable;
import game.products.crops.ConsumableProduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Single-responsibility filter that decides whether a tile holds food that a
 * foraging creature is allowed to pick up.
 * <p>
 * The default diet is portable REQ1 produce: strawberries, cauliflowers and
 * melons. Legacy foods require stamina, which SporeBeast does not have.
 * A supplied diet can extend this policy without changing search or movement.
 *
 * @author Ding Peiyuncheng (REQ5 - Part B: Foraging)
 */
public class EdibleFoodFilter
{
    private final Predicate<Item> diet;

    /** Creates the diet supported by a health-only SporeBeast. */
    public EdibleFoodFilter() {
        this(item -> item instanceof ConsumableProduce);
    }

    /** @param diet additional food policy; accepted food must support this actor */
    public EdibleFoodFilter(Predicate<Item> diet) {
        this.diet = Objects.requireNonNull(diet);
    }

    /** @return whether the item can be collected and eaten under this diet */
    public boolean isEdible(Item item) {
        return item instanceof Consumable && item.hasAbility(ItemAbility.PORTABLE)
                && diet.test(item);
    }

    /**
     * Returns the edible items lying on a tile.
     *
     * @param location the tile to inspect
     * @return edible items on the tile (empty list if none)
     */
    public List<Item> edibleFoodAt(Location location)
    {
        List<Item> edible = new ArrayList<>();
        for (Item item : location.getItems())
        {
            if (isEdible(item))
            {
                edible.add(item);
            }
        }
        return edible;
    }

    /**
     * @param location the tile to inspect
     * @return true if the tile holds at least one edible item
     */
    public boolean hasEdibleFood(Location location)
    {
        return !edibleFoodAt(location).isEmpty();
    }
}
