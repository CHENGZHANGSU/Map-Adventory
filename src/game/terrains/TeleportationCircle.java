package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.interfaces.Teleportable;
import game.products.minerals.Fire;

import java.util.Objects;

/**
 * A fixed teleportation circle that sends an actor to a connected location.
 * After teleportation, the locations surrounding the destination are
 * set on fire for two turns.
 *
 * @author Zizhuo Wang
 */
public class TeleportationCircle extends Ground implements Teleportable {

    private static final int FIRE_LIFESPAN = 2;
    private static final int FIRE_BURN_DURATION = 3;
    private static final int FIRE_DAMAGE = 5;

    private Location destination;

    /**
     * Creates a teleportation circle.
     */
    public TeleportationCircle() {
        super('O', "Teleportation Circle");
    }

    /**
     * Sets the fixed destination of this teleportation circle.
     *
     * @param destination location reached after teleportation
     * @throws NullPointerException if destination is null
     * @throws IllegalStateException if a destination was already assigned
     */
    public void setDestination(Location destination) {
        if (this.destination != null) {
            throw new IllegalStateException(
                    "The teleportation circle destination has already been set.");
        }

        this.destination = Objects.requireNonNull(destination);
    }

    /**
     * Provides a teleport action when the actor is standing on this circle.
     *
     * @param actor the actor attempting to teleport
     * @param location the location containing this circle
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
            actions.add(new TeleportAction(
                    this,
                    destination,
                    "Teleportation Circle"));
        }

        return actions;
    }

    /**
     * Teleports the actor and ignites every location adjacent to the
     * destination. The destination tile itself is not ignited.
     *
     * @param actor the actor being teleported
     * @param destination the destination location
     * @param map the actor's current map
     * @return a description of the teleportation result
     */
    @Override
    public String teleport(
            Actor actor,
            Location destination,
            GameMap map) {

        if (destination == null) {
            return "The teleportation circle is not connected.";
        }

        if (destination.containsAnActor()) {
            return "The teleportation circle destination is occupied.";
        }

        map.moveActor(actor, destination);

        for (Exit exit : destination.getExits()) {
            Location adjacentLocation = exit.getDestination();

            if (!containsFire(adjacentLocation)) {
                adjacentLocation.addItem(new Fire(
                        FIRE_LIFESPAN,
                        FIRE_BURN_DURATION,
                        FIRE_DAMAGE));
            }
        }

        return actor + " teleported, setting the arrival area ablaze!";
    }

    /**
     * Checks whether a location already contains fire.
     *
     * @param location location to inspect
     * @return true if fire is already present
     */
    private boolean containsFire(Location location) {
        return location.getItems()
                .stream()
                .anyMatch(item -> item instanceof Fire);
    }
}
