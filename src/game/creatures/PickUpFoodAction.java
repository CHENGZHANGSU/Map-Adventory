package game.creatures;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creatures.food.EdibleFoodFilter;

/**
 * Picks a single edible item up off the ground into the foraging creature's
 * inventory. Returned by {@link ForagingState} when the beast is standing on
 * food.
 *
 * @author Ding Peiyuncheng (REQ5 - Part B: Foraging)
 */
public class PickUpFoodAction extends Action
{
    private final Location here;
    private final Item food;
    private final EdibleFoodFilter foodFilter;

    public PickUpFoodAction(Location here, Item food)
    {
        this(here, food, new EdibleFoodFilter());
    }

    /** Uses the same diet as the state that selected this item. */
    public PickUpFoodAction(Location here, Item food, EdibleFoodFilter foodFilter) {
        this.here = here;
        this.food = food;
        this.foodFilter = foodFilter;
    }

    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (!map.contains(actor) || map != here.map() || map.locationOf(actor) != here
                || !actor.isConscious() || !here.getItems().contains(food)
                || !foodFilter.isEdible(food)) {
            return actor + " cannot collect " + food + ": the food is no longer available here.";
        }
        here.removeItem(food);
        actor.getInventory().add(food);
        return actor + " forages and picks up " + food + ".";
    }

    @Override
    public String menuDescription(Actor actor)
    {
        return actor + " picks up " + food;
    }
}
