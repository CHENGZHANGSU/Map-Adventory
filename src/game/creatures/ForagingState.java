package game.creatures;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.SporeBeast;
import game.creatures.food.EdibleFoodFilter;
import game.creatures.food.NearestEdibleFoodFinder;
import game.creatures.path.DeterministicPathfinder;
import game.interfaces.CreatureState;
import game.interfaces.Consumable;
import game.actions.ConsumeAction;
import game.states.BaseStates;
import game.states.fleeing.ThreatScanner;
import game.creatures.food.FoodLureEffect;

import java.util.List;

/**
 * Foraging state of the stateful creature (REQ5 Part B).
 * <p>
 * Behaviour: each turn the beast
 * <ol>
 *     <li>eats one carried food item, otherwise collects one from its tile;</li>
 *     <li>otherwise it searches the map deterministically (BFS) for the nearest
 *         reachable edible food and takes the shortest next step toward it;</li>
 *     <li>if there is no reachable food, it stays put.</li>
 * </ol>
 * Nearby threats take priority: two or more lead to fleeing, one to territorial.
 * With no threats and no carried/reachable food, it returns to camouflage.
 * All targeting and transition decisions are deterministic
 * (no {@code Random}); they are re-evaluated every turn, so if another actor
 * takes the target food first the beast recomputes a fresh target.
 *
 * @author Ding Peiyuncheng (REQ5 - Part B: Foraging)
 */
public class ForagingState implements CreatureState
{
    private final EdibleFoodFilter foodFilter;
    private final NearestEdibleFoodFinder foodFinder;
    private final DeterministicPathfinder pathfinder;
    private final ThreatScanner threatScanner = new ThreatScanner();
    private static final int THREAT_RADIUS = 2;

    public ForagingState()
    {
        this(new EdibleFoodFilter());
    }

    /** Shares one diet between searching, picking up and eating. */
    public ForagingState(EdibleFoodFilter foodFilter) {
        this(foodFilter, new NearestEdibleFoodFinder(foodFilter), new DeterministicPathfinder());
    }

    public ForagingState(EdibleFoodFilter foodFilter,
                         NearestEdibleFoodFinder foodFinder,
                         DeterministicPathfinder pathfinder)
    {
        this.foodFilter = foodFilter;
        this.foodFinder = foodFinder;
        this.pathfinder = pathfinder;
    }

    /**
     * Computes the beast's foraging action for this turn.
     *
     * @param creature the foraging creature
     * @param map      the map it is on
     * @return one consume, pick-up, move, or do-nothing action
     */
    @Override
    public Action createTurnAction(SporeBeast creature, GameMap map)
    {
        if (!map.contains(creature) || !creature.isConscious()) {
            return new DoNothingAction();
        }
        Location here = map.locationOf(creature);

        for (Item item : creature.getInventory().getItems()) {
            if (foodFilter.isEdible(item)) {
                return new ConsumeAction((Consumable) item);
            }
        }

        // 1. Already on top of food -> pick it up.
        List<Item> foodHere = foodFilter.edibleFoodAt(here);
        if (!foodHere.isEmpty())
        {
            return new PickUpFoodAction(here, foodHere.get(0), foodFilter);
        }

        // 2. Search for the nearest reachable edible food.
        Location target = foodFinder.find(creature, here);
        if (target != null)
        {
            Location step = pathfinder.nextStep(creature, here, target);
            if (step != null)
            {
                Action move = moveToward(creature, here, step);
                if (move != null)
                {
                    return move;
                }
            }
        }

        // 3. No reachable food -> stay still.
        return new DoNothingAction();
    }

    /**
     * Deterministic transition condition: leave foraging once there is no edible
     * food reachable anywhere on the map (the larder is exhausted).
     *
     * @param creature the foraging creature
     * @param map      the map it is on
     * @return true if the beast should transition out of foraging
     */
    public boolean wantsToReturnToCamouflage(Actor creature, GameMap map)
    {
        if (creature.getInventory().getItems().stream().anyMatch(foodFilter::isEdible)) {
            return false;
        }
        Location here = map.locationOf(creature);
        if (foodFilter.hasEdibleFood(here))
        {
            return false; // still about to pick something up
        }
        return foodFinder.find(creature, here) == null;
    }

    /** Threats take priority over food; no transition performs a turn action. */
    @Override
    public CreatureState getNextState(SporeBeast creature, GameMap map) {
        if (!map.contains(creature) || !creature.isConscious()) {
            return this;
        }
        int threats = threatScanner.findNearbyThreats(
                creature, map.locationOf(creature), THREAT_RADIUS).size();
        if (threats >= 2) {
            return BaseStates.getFleeingState();
        }
        if (threats == 1) {
            return BaseStates.getTerritorialState();
        }
        return wantsToReturnToCamouflage(creature, map)
                ? BaseStates.getCamouflagedState() : this;
    }

    /** A scent pulse gathers nearby reachable food once when foraging begins. */
    @Override
    public void onEnter(SporeBeast creature, GameMap map) {
        new FoodLureEffect(foodFilter, pathfinder).apply(creature, map);
    }

    /**
     * Builds the engine move action that steps the beast onto {@code step}.
     */
    private Action moveToward(Actor creature, Location here, Location step)
    {
        for (Exit exit : here.getExits())
        {
            Location dest = exit.getDestination();
            if (dest == step)
            {
                Action move = dest.getMoveAction(creature, exit.getName(), exit.getHotKey());
                if (move != null)
                {
                    return move;
                }
            }
        }
        return null;
    }
}
