package game.creatures.path;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Deterministic shortest-path movement: given a start and a target tile, return
 * the adjacent tile to step on next.
 * <p>
 * Uses BFS (fewest turns) with a fixed neighbour order (x then y). Ties are
 * broken deterministically, so the beast never makes a random movement choice.
 *
 * @author Ding Peiyuncheng (REQ5 - Part B: Foraging)
 */
public class DeterministicPathfinder
{
    /**
     * @param beast  the foraging actor (used to test enterability)
     * @param from   the beast's current tile
     * @param target the tile to reach
     * @return the next tile on the shortest path, or {@code null} if unreachable
     */
    public Location nextStep(Actor beast, Location from, Location target)
    {
        if (from.map() != target.map() || from == target)
        {
            return null;
        }

        return findStep(beast, from, location -> location == target);
    }

    /** Finds the shortest legal step toward a tile adjacent to an occupied target. */
    public Location nextStepToAdjacent(Actor beast, Location from, Location target) {
        if (from.map() != target.map() || from == target) return null;
        return findStep(beast, from, location -> location.getExits().stream()
                .anyMatch(exit -> exit.getDestination() == target));
    }

    private Location findStep(Actor beast, Location from, Predicate<Location> reached) {
        Map<Location, Location> parent = new HashMap<>();
        Deque<Location> queue = new ArrayDeque<>();
        parent.put(from, null);
        queue.add(from);

        Location target = null;
        while (!queue.isEmpty())
        {
            Location current = queue.poll();
            if (reached.test(current))
            {
                target = current;
                break;
            }

            for (Location next : sortedEnterableNeighbours(beast, current))
            {
                if (!parent.containsKey(next))
                {
                    parent.put(next, current);
                    queue.add(next);
                }
            }
        }

        if (target == null || target == from)
        {
            return null; // unreachable
        }

        // Walk backwards from target to the first step after `from`.
        Location cursor = target;
        Location prev = parent.get(cursor);
        while (prev != null && prev != from)
        {
            cursor = prev;
            prev = parent.get(cursor);
        }
        return cursor;
    }

    private java.util.List<Location> sortedEnterableNeighbours(Actor beast, Location current)
    {
        java.util.List<Location> result = new java.util.ArrayList<>();
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
