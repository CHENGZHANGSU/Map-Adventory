package game.products.minerals;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakAction;
import game.interfaces.Breakable;
import game.interfaces.Breaker;

import java.util.Objects;
import java.util.Random;

/**
 * A portable geode that can be broken on the ground or from an actor's
 * inventory using a suitable breaking tool.
 * Breaking a geode has a 50% chance of producing a diamond. Otherwise,
 * it explodes, damaging nearby actors and setting adjacent locations on
 * fire.
 *
 * @author Zizhuo Wang
 */
public class Geode extends Item implements Breakable {

    private static final int EXPLOSION_DAMAGE = 25;
    private static final int FIRE_LIFESPAN = 5;
    private static final int FIRE_BURN_DURATION = 4;
    private static final int FIRE_BURN_DAMAGE = 10;

    private final Random random;

    /**
     * Creates a portable geode with a new random number generator.
     */
    public Geode() {
        this(new Random());
    }

    /**
     * Creates a portable geode with the supplied random number generator.
     *
     * @param random random number generator used for the break outcome
     * @throws NullPointerException if random is null
     */
    public Geode(Random random) {
        super("Geode", 'g');
        this.random = Objects.requireNonNull(random);
        makePortable();
    }

    /**
     * Provides a break action when the geode is on the ground.
     * The engine's ground-item action method does not provide the acting
     * actor, so tool possession is validated when the action executes.
     *
     * @param location location containing the geode
     * @return actions available for the ground geode
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList actions = super.allowableActions(location);
        actions.add(new BreakAction(this, location));
        return actions;
    }

    /**
     * Provides a break action when the geode is carried with a suitable tool.
     *
     * @param owner actor carrying the geode
     * @param map map containing the actor
     * @return actions available for the carried geode
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);

        if (hasBreakingTool(owner)) {
            actions.add(new BreakAction(this, map.locationOf(owner)));
        }

        return actions;
    }

    /**
     * Removes the geode and resolves either its diamond or explosion outcome.
     *
     * @param actor actor breaking the geode
     * @param location geode location, or actor location when carried
     * @param map current game map
     * @return description of the break result
     */
    @Override
    public String breakEntity(
            Actor actor,
            Location location,
            GameMap map) {

        if (!hasBreakingTool(actor)) {
            return actor
                    + " cannot break the geode without a suitable tool.";
        }

        Location activeLocation = removeGeode(actor, location, map);

        if (activeLocation == null) {
            return "The geode is no longer available to break.";
        }

        if (random.nextBoolean()) {
            activeLocation.addItem(new Diamond());
            return actor + " cracked the geode and found a diamond!";
        }

        explode(actor, activeLocation);

        return actor + " broke the geode, causing a violent explosion!";
    }

    /**
     * Removes the geode from the actor's inventory or its ground location.
     *
     * @param actor actor breaking the geode
     * @param location expected ground location of the geode
     * @param map current game map
     * @return explosion centre, or null if the geode was unavailable
     */
    private Location removeGeode(
            Actor actor,
            Location location,
            GameMap map) {

        if (actor.getInventory().getItems().contains(this)) {
            actor.getInventory().remove(this);
            return map.locationOf(actor);
        }

        if (location.getItems().contains(this)) {
            location.removeItem(this);
            return location;
        }

        return null;
    }

    /**
     * Damages the actor who broke the geode and every adjacent actor, then
     * sets all adjacent locations on fire.
     *
     * @param actor actor who broke the geode
     * @param explosionCentre location at the centre of the explosion
     */
    private void explode(Actor actor, Location explosionCentre) {
        actor.hurt(EXPLOSION_DAMAGE);

        for (Exit exit : explosionCentre.getExits()) {
            Location adjacentLocation = exit.getDestination();

            if (adjacentLocation.containsAnActor()
                    && adjacentLocation.getActor() != actor) {
                adjacentLocation.getActor().hurt(EXPLOSION_DAMAGE);
            }

            ignite(adjacentLocation);
        }
    }

    /**
     * Adds explosion fire to a location unless fire is already present.
     *
     * @param location location to ignite
     */
    private void ignite(Location location) {
        boolean alreadyBurning = location.getItems()
                .stream()
                .anyMatch(item -> item instanceof Fire);

        if (!alreadyBurning) {
            location.addItem(new Fire(
                    FIRE_LIFESPAN,
                    FIRE_BURN_DURATION,
                    FIRE_BURN_DAMAGE));
        }
    }

    /**
     * Checks whether an actor carries a tool capable of breaking entities.
     *
     * @param actor actor whose inventory is checked
     * @return true if the actor carries a breaking tool
     */
    private boolean hasBreakingTool(Actor actor) {
        for (Item item : actor.getInventory().getItems()) {
            if (item instanceof Breaker) {
                return true;
            }
        }

        return false;
    }
}
