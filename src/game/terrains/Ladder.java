package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.interfaces.Teleportable;

import java.util.Objects;
import java.util.Random;

/**
 * A fixed ladder that allows the farmer to travel between maps.
 * Using a ladder gives the farmer a 50% chance of falling and
 * receiving 25 damage. The farmer still reaches the destination after
 * falling.
 *
 * @author Zizhuo Wang
 */
public class Ladder extends Ground implements Teleportable {

    /**
     * The location connected to this ladder.
     */
    private Location destination;

    /**
     * Random number generator used for the falling chance.
     */
    private final Random random;

    /**
     * Creates a ladder using a new random number generator.
     */
    public Ladder() {
        this(new Random());
    }

    /**
     * Creates a ladder using the supplied random number generator.
     *
     * @param random random number generator used for the falling chance
     */
    public Ladder(Random random) {
        super('=', "Ladder");
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Sets the destination connected to this ladder.
     * A ladder destination can only be assigned once because ladders
     * are fixed and cannot be moved.
     *
     * @param destination location reached when the ladder is used
     * @throws NullPointerException if the destination is null
     * @throws IllegalStateException if a destination has already been set
     */
    public void setDestination(Location destination) {
        if (this.destination != null) {
            throw new IllegalStateException(
                    "The ladder destination has already been set.");
        }

        this.destination = Objects.requireNonNull(destination);
    }

    /**
     * Provides a ladder action when the actor is standing on this ladder.
     *
     * @param actor the actor attempting to use the ladder
     * @param location the location containing this ladder
     * @param direction the direction associated with this ground
     * @return the available actions
     */
    @Override
    public ActionList allowableActions(
            Actor actor,
            Location location,
            String direction) {

        ActionList actions =
                super.allowableActions(actor, location, direction);

        if (location.containsAnActor()
                && location.getActor() == actor
                && destination != null) {
            actions.add(new TeleportAction(this, destination, "Ladder"));
        }

        return actions;
    }

    /**
     * Teleports the actor to the ladder's connected destination.
     *
     * @param actor the actor using the ladder
     * @param destination the destination location
     * @param map the actor's current map
     * @return a description of the result
     */
    @Override
    public String teleport(
            Actor actor,
            Location destination,
            GameMap map) {

        if (destination == null) {
            return "The ladder is not connected to another location.";
        }

        if (destination.containsAnActor()) {
            return "The ladder destination is occupied.";
        }

        String result = actor + " climbs the ladder.";

        if (random.nextBoolean()) {
            actor.hurt(25);
            result += " They slip and fall, taking 25 damage!";
        }

        map.moveActor(actor, destination);
        return result;
    }
}