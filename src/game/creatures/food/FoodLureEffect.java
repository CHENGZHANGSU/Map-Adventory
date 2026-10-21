package game.creatures.food;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.creatures.path.DeterministicPathfinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * On entry to foraging, a scent pulse pulls up to three nearby food items onto
 * the beast's tile. Only unoccupied, reachable tiles within radius two qualify.
 * Items are moved, never copied or consumed. Inventories and terrain are untouched.
 */
public final class FoodLureEffect {
    private static final int RADIUS = 2;
    private static final int ITEM_LIMIT = 3;
    private final EdibleFoodFilter foodFilter;
    private final DeterministicPathfinder pathfinder;

    public FoodLureEffect(EdibleFoodFilter foodFilter, DeterministicPathfinder pathfinder) {
        this.foodFilter = foodFilter;
        this.pathfinder = pathfinder;
    }

    /** @return the number of actual items relocated by this entry effect */
    public int apply(Actor creature, GameMap map) {
        if (!map.contains(creature) || !creature.isConscious()) {
            return 0;
        }
        Location origin = map.locationOf(creature);
        List<Location> candidates = new ArrayList<>(origin.getNearbyLocations(RADIUS));
        candidates.sort(Comparator.comparingInt(Location::x).thenComparingInt(Location::y));
        int moved = 0;
        for (Location source : candidates) {
            if (!source.canActorEnter(creature) || !foodFilter.hasEdibleFood(source)
                    || pathfinder.nextStep(creature, origin, source) == null) {
                continue;
            }
            for (Item food : foodFilter.edibleFoodAt(source)) {
                source.removeItem(food);
                origin.addItem(food);
                if (++moved == ITEM_LIMIT) {
                    return moved;
                }
            }
        }
        return moved;
    }
}
