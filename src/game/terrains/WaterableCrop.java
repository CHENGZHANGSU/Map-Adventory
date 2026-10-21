package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.WaterAction;
import game.interfaces.Waterable;
import game.tools.ToolAbility;
import game.tools.ToolAccess;

import java.util.Random;

/** A crop that remembers its own successful waterings. */
public abstract class WaterableCrop extends Crop implements Waterable {
    private final int waterCost;
    private int wateringCount;

    /**
     * Creates a crop with separate harvesting and watering costs.
     * @param symbol map symbol
     * @param name displayed name
     * @param harvestCost stamina required to harvest
     * @param waterCost stamina required to water
     * @param random source of random outcomes
     */
    protected WaterableCrop(char symbol, String name, int harvestCost, int waterCost, Random random) {
        super(symbol, name, harvestCost, random);
        this.waterCost = waterCost;
    }

    /** @return successful waterings received by this individual plant */
    public final int getWateringCount() {
        return wateringCount;
    }

    /** Satisfies Waterable: checks the carried tool and stamina, without side effects. */
    @Override
    public boolean requirementMet(Actor actor) {
        return wateringFailureFor(actor) == null;
    }

    /** Checks the carried tool and stamina once, without spending resources. */
    private String wateringFailureFor(Actor actor) {
        if (!ToolAccess.hasCarriedTool(actor, ToolAbility.WATER)) {
            return "Cannot water " + this + ": a watering tool is required.";
        }
        if (!actor.hasStatistic(ActorStatistics.STAMINA)
                || actor.getStatistic(ActorStatistics.STAMINA) < waterCost) {
            return "Cannot water " + this + ": requires " + waterCost + " stamina.";
        }
        return null;
    }

    @Override
    public final String wateredBy(Actor actor, GameMap map) {
        Location location = locationFor(actor, map);
        if (location == null) {
            return "Cannot water " + this + ": the crop is no longer within reach.";
        }
        String failure = wateringFailureFor(actor);
        if (failure != null) {
            return failure;
        }
        actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, waterCost);
        wateringCount++;
        return waterAt(actor, location);
    }

    /**
     * Applies an effect after payment; the count includes this watering.
     * @param actor watering actor
     * @param location location occupied by the crop
     * @return description of the watering outcome
     */
    protected abstract String waterAt(Actor actor, Location location);

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);
        if (actor.hasStatistic(ActorStatistics.STAMINA)
                && ToolAccess.hasCarriedTool(actor, ToolAbility.WATER)) {
            actions.add(new WaterAction(this, direction));
        }
        return actions;
    }
}
