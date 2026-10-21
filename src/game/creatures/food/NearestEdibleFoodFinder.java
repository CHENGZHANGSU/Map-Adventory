package game.creatures.food;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Deterministically finds the reachable tile with edible food that is closest
 * to a starting tile.
 * <p>
 * Breadth-first search guarantees the closest food (fewest turns of movement).
 * Neighbours are expanded in a fixed order (sorted by x then y) so the chosen
 * food never depends on {@code Random} - the next state / target decision is
 * reproducible, as required by REQ5.
 *
 * @author Ding Peiyuncheng (REQ5 - Part B: Foraging)
 */
public class NearestEdibleFoodFinder
{
    private final EdibleFoodFilter foodFilter;

    public NearestEdibleFoodFinder()
    {
        this(new EdibleFoodFilter());
    }

    public NearestEdibleFoodFinder(EdibleFoodFilter foodFilter)
    {
        this.foodFilter = foodFilter;
    }

    /**
     * Searches outwards from {@code from} for the nearest reachable tile that
     * holds edible food.
     *
     * @param beast the foraging actor (used to test whether a tile is enterable)
     * @param from  the beast's current tile
     * @return the nearest food tile, or {@code null} if no reachable food exists
     */
    public Location find(Actor beast, Location from)
    {
        Set<Location> visited = new HashSet<>();
        Deque<Location> queue = new ArrayDeque<>();

        visited.add(from);
        queue.add(from);

        while (!queue.isEmpty())
        {
            Location current = queue.poll();

            // Food on the starting tile is already at distance zero.
            if (foodFilter.hasEdibleFood(current))
            {
                return current;
            }

            for (Location next : sortedEnterableNeighbours(beast, current))
            {
                if (visited.add(next))
                {
                    queue.add(next);
                }
            }
        }
        return null;
    }

    /**
     * Enterable neighbours in a deterministic order (x ascending, then y).
     */
    private List<Location> sortedEnterableNeighbours(Actor beast, Location current)
    {
        List<Location> result = new ArrayList<>();
        for (Exit exit : current.getExits())
        {
            Location dest = exit.getDestination();
            if (dest.map() == current.map() && dest.canActorEnter(beast))
            {
                result.add(dest);
            }
        }
        result.sort((a, b) -> {
            int byX = Integer.compare(a.x(), b.x());
            return byX != 0 ? byX : Integer.compare(a.y(), b.y());
        });
        return result;
    }

}
