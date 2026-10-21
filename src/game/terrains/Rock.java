package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakAction;
import game.interfaces.Breakable;
import game.interfaces.Breaker;
import game.products.minerals.Stone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * An impassable rock that can be broken using a suitable breaking tool.
 * Breaking a rock replaces it with dirt and drops one stone. There is
 * also a 50% chance that a shockwave throws the actor onto a randomly
 * selected valid location adjacent to the rock.
 *
 * @author Zizhuo Wang
 */
public class Rock extends Ground implements Breakable {

    private static final String ROCK_NAME = "Rock";

    private final Random random;

    /**
     * Creates a rock with a new random number generator.
     */
    public Rock() {
        this(new Random());
    }

    /**
     * Creates a rock with the supplied random number generator.
     * This constructor allows predictable random behaviour during
     * testing.
     *
     * @param random random number generator used for the shockwave
     * @throws NullPointerException if random is null
     */
    public Rock(Random random) {
        super('R', ROCK_NAME);
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Prevents actors from entering the rock's location.
     *
     * @param actor actor attempting to enter
     * @return false because rocks are impassable
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Provides a break action when the actor is carrying a suitable
     * breaking tool.
     *
     * @param actor actor attempting to interact with the rock
     * @param location location containing the rock
     * @param direction direction of the rock from the actor
     * @return available actions for the actor
     */
    @Override
    public ActionList allowableActions(
            Actor actor,
            Location location,
            String direction) {

        ActionList actions =
                super.allowableActions(actor, location, direction);

        if (hasBreakingTool(actor)) {
            actions.add(new BreakAction(this, location));
        }

        return actions;
    }

    /**
     * Breaks the rock, replaces it with dirt, and drops a stone.
     * There is a 50% chance of producing a shockwave. When possible,
     * the shockwave moves the actor onto a randomly selected unoccupied
     * and enterable location adjacent to the broken rock.
     *
     * @param actor actor breaking the rock
     * @param location location containing the rock
     * @param map current game map
     * @return description of the breaking result
     */
    @Override
    public String breakEntity(
            Actor actor,
            Location location,
            GameMap map) {

        if (!hasBreakingTool(actor)) {
            return actor + " cannot break the rock without a suitable tool.";
        }

        if (location.getGround() != this) {
            return "The rock is no longer at this location.";
        }

        location.setGround(new Dirt());
        location.addItem(new Stone());

        String result = actor + " broke the rock and uncovered a stone.";

        if (!random.nextBoolean()) {
            return result;
        }

        List<Location> validDestinations =
                findValidShockwaveDestinations(actor, location);

        if (validDestinations.isEmpty()) {
            return result
                    + " A shockwave erupted, but there was nowhere to be thrown!";
        }

        Location destination = validDestinations.get(
                random.nextInt(validDestinations.size()));

        map.moveActor(actor, destination);

        return result
                + " A shockwave threw them onto an adjacent tile!";
    }

    /**
     * Checks whether an actor is carrying a tool capable of breaking
     * breakable entities.
     *
     * @param actor actor whose inventory is checked
     * @return true if the actor is carrying a breaking tool
     */
    private boolean hasBreakingTool(Actor actor) {
        for (Item item : actor.getInventory().getItems()) {
            if (item instanceof Breaker) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds all unoccupied and enterable locations adjacent to the rock.
     *
     * @param actor actor that may be moved by the shockwave
     * @param rockLocation location of the broken rock
     * @return valid adjacent shockwave destinations
     */
    private List<Location> findValidShockwaveDestinations(
            Actor actor,
            Location rockLocation) {

        List<Location> validDestinations = new ArrayList<>();

        for (Exit exit : rockLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();

            if (!adjacentLocation.containsAnActor()
                    && adjacentLocation.getGround().canActorEnter(actor)) {
                validDestinations.add(adjacentLocation);
            }
        }

        return validDestinations;
    }
}
