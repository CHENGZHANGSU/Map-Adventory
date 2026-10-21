package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.HarvestAction;
import game.interfaces.Harvestable;
import game.interfaces.Plantable;

import java.util.Objects;
import java.util.Random;

/** A planted crop, with shared harvesting checks and removal behaviour. */
public abstract class Crop extends Ground implements Harvestable, Plantable {
    protected final Random random;
    private final int harvestCost;

    /**
     * Creates an already-grown crop.
     * @param displayChar map symbol
     * @param name displayed crop name
     * @param harvestCost stamina charged for an eligible harvest
     * @param random source of random outcomes
     */
    protected Crop(char displayChar, String name, int harvestCost, Random random) {
        super(displayChar, name);
        this.harvestCost = harvestCost;
        this.random = Objects.requireNonNull(random);
    }

    /** Satisfies Harvestable: checks readiness and stamina, without side effects. */
    @Override
    public boolean requirementMet(Actor actor) {
        return harvestFailureFor(actor) == null;
    }

    /** Checks readiness and stamina once, without changing the crop or actor. */
    private String harvestFailureFor(Actor actor) {
        String failure = harvestFailure();
        if (failure != null) {
            return failure;
        }
        if (!actor.hasStatistic(ActorStatistics.STAMINA)
                || actor.getStatistic(ActorStatistics.STAMINA) < harvestCost) {
            return "Cannot harvest " + this + ": requires " + harvestCost + " stamina.";
        }
        return null;
    }

    /**
     * Finds this exact plant, including when the actor stands on it at a map edge.
     * @param actor actor attempting the interaction
     * @param map actor's current map
     * @return the crop's reachable location, or null if absent or out of reach
     */
    protected Location locationFor(Actor actor, GameMap map) {
        if (!map.contains(actor)) {
            return null;
        }
        Location here = map.locationOf(actor);
        if (here.getGround() == this) {
            return here;
        }
        return here.getExits().stream()
                .map(exit -> exit.getDestination())
                .filter(location -> location.getGround() == this)
                .findFirst().orElse(null);
    }

    /**
     * Checks crop-specific readiness before charging stamina, without side effects
     * or random draws. Used both when querying eligibility and when executing.
     * @return a failure explanation, or null when the crop is ready
     */
    protected String harvestFailure() {
        return null;
    }

    @Override
    public final String harvestBy(Actor actor, GameMap map) {
        Location location = locationFor(actor, map);
        if (location == null) {
            return "Cannot harvest " + this + ": the crop is no longer within reach.";
        }
        String failure = harvestFailureFor(actor);
        if (failure != null) {
            return failure;
        }
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, harvestCost);
        return harvestAt(actor, location);
    }

    /**
     * Applies the crop-specific result after validation and payment.
     * @param actor harvesting actor
     * @param location location occupied by the crop
     * @return description of the harvest outcome
     */
    protected abstract String harvestAt(Actor actor, Location location);

    /**
     * Every removed crop leaves dirt, regardless of the reason for removal.
     * @param location location occupied by the crop
     */
    protected final void removeFrom(Location location) {
        location.setGround(new Dirt());
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (actor.hasStatistic(ActorStatistics.STAMINA)) {
            actions.add(new HarvestAction(this, direction));
        }
        return actions;
    }
}
