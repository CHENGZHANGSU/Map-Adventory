package game.terrains;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.products.crops.Melon;
import game.util.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** A melon may explode on harvest; watering reduces its blast damage. */
public class MelonCrop extends WaterableCrop {
    private static final int BASE_EXPLOSION_DAMAGE = 20;
    private static final int STAMINA_INCREASE_CHANCE_PERCENT = 50;
    private static final int EXPLOSION_CHANCE_PERCENT = 50;

    /** Creates an unwatered melon plant. */
    public MelonCrop() {
        this(new Random());
    }

    /**
     * Creates a plant with a supplied random source.
     * @param random source for stamina increases and explosions
     */
    public MelonCrop(Random random) {
        super('M', "Melon crop", 5, 1, random);
    }

    @Override
    protected String waterAt(Actor actor, Location location) {
        if (random.nextInt(100) < STAMINA_INCREASE_CHANCE_PERCENT) {
            Statistics.increaseMaximum(actor, ActorStatistics.STAMINA, 1);
            return actor + " waters " + this + ": maximum stamina increases by 1.";
        }
        return actor + " waters " + this + ".";
    }

    @Override
    protected String harvestAt(Actor actor, Location location) {
        removeFrom(location);
        if (random.nextInt(100) >= EXPLOSION_CHANCE_PERCENT) {
            actor.getInventory().add(new Melon());
            return actor + " harvests one melon. The plant leaves dirt.";
        }
        int damage = Math.max(0, BASE_EXPLOSION_DAMAGE - getWateringCount());
        List<Location> blastArea = new ArrayList<>(location.getNearbyLocations(1));
        blastArea.add(location);
        StringBuilder result = new StringBuilder("The melon explodes for " + damage + " damage!");
        for (Location target : blastArea) {
            if (target.containsAnActor()) {
                Actor victim = target.getActor();
                victim.hurt(damage);
                result.append("\n").append(victim).append(" takes ").append(damage).append(" damage.");
                if (!victim.isConscious()) {
                    result.append("\n").append(victim.unconscious(target.map()));
                }
            }
        }
        return result.toString();
    }
}
