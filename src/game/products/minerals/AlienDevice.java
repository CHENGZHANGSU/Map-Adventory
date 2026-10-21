package game.products.minerals;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.interfaces.Teleportable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A portable alien device that teleports its owner to one of several configured
 * destinations.
 * Each use has a 50% chance of malfunctioning and moving the actor to a
 * random valid location on their current map instead.
 *
 * @author Zizhuo Wang
 */
public class AlienDevice extends Item implements Teleportable {

    private final List<Location> destinations = new ArrayList<>();

    private final Random random;

    /**
     * Creates an alien device with a new random number generator.
     */
    public AlienDevice() {
        this(new Random());
    }

    /**
     * Creates an alien device with the supplied random number generator.
     *
     * @param random random number generator used for malfunction behaviour
     * @throws NullPointerException if random is null
     */
    public AlienDevice(Random random) {
        super("Alien Device", 'A');
        this.random = Objects.requireNonNull(random);
        makePortable();
    }

    /**
     * Adds a fixed destination that the device can offer to its owner.
     *
     * @param destination location that can be selected for teleportation
     * @throws NullPointerException if destination is null
     */
    public void addDestination(Location destination) {
        Location validDestination = Objects.requireNonNull(destination);

        if (!destinations.contains(validDestination)) {
            destinations.add(validDestination);
        }
    }

    /**
     * Provides one teleport action for every configured destination while the
     * alien device is carried by an actor.
     *
     * @param owner actor carrying the alien device
     * @param map current map of the actor
     * @return teleport actions available from the device
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);

        for (Location destination : destinations) {
            String destinationName = destination.map().toString();
            String actionName =
                    "Alien Device (destination: " + destinationName + ")";

            actions.add(new TeleportAction(this, destination, actionName));
        }

        return actions;
    }

    /**
     * Attempts to teleport the actor to the selected destination.
     * On malfunction, the actor is moved to a random valid location on
     * their current map rather than the selected destination.
     *
     * @param actor actor using the alien device
     * @param destination selected teleport destination
     * @param map actor's current map
     * @return description of the teleportation result
     */
    @Override
    public String teleport(
            Actor actor,
            Location destination,
            GameMap map) {

        if (random.nextBoolean()) {
            Location randomDestination =
                    findRandomValidLocation(actor, map);

            if (randomDestination == null) {
                return "The Alien Device malfunctioned, but there was no "
                        + "valid location to teleport to.";
            }

            map.moveActor(actor, randomDestination);

            return "The Alien Device malfunctioned! "
                    + actor
                    + " was teleported to a random location on "
                    + map
                    + ".";
        }

        if (destination == null) {
            return "The Alien Device has no valid destination configured.";
        }

        if (destination.containsAnActor()) {
            return "The selected Alien Device destination is occupied.";
        }

        map.moveActor(actor, destination);

        return actor + " successfully used the Alien Device.";
    }

    /**
     * Selects a random unoccupied and enterable location on the current map.
     *
     * @param actor actor to be moved
     * @param map map on which the actor currently stands
     * @return a random valid location, or null when none exists
     */
    private Location findRandomValidLocation(Actor actor, GameMap map) {
        List<Location> validLocations = new ArrayList<>();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location location = map.at(x, y);

                if (!location.containsAnActor()
                        && location.getGround().canActorEnter(actor)) {
                    validLocations.add(location);
                }
            }
        }

        if (validLocations.isEmpty()) {
            return null;
        }

        return validLocations.get(random.nextInt(validLocations.size()));
    }
}
