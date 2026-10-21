package game.terrains;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.products.crops.Parsnip;

import java.util.List;
import java.util.Random;

/**
 * The original parsnip crop, now also supporting risky, healing waterings.
 * @author Su Chengzhang (original implementation)
 */
public class ParsnipCrop extends WaterableCrop {
    private static final int WATER_HEAL_AMOUNT = 20;
    private static final int BASE_DESTRUCTION_CHANCE_PERCENT = 10;
    private static final int REPLANT_CHANCE_PERCENT = 50;

    /** Creates a parsnip plant with no previous waterings. */
    public ParsnipCrop() {
        this(new Random());
    }

    /**
     * Creates a plant with a supplied random source.
     * @param random source shared by this plant and its offspring for damage and spawning;
     *               each plant still has its own watering count
     */
    public ParsnipCrop(Random random) {
        super('P', "Parsnip crop", 1, 1, random);
    }

    @Override
    protected String waterAt(Actor actor, Location location) {
        actor.heal(WATER_HEAL_AMOUNT);
        // Count includes this watering: the first watering has an 11% risk.
        boolean destroyed = random.nextInt(100)
                < Math.min(100, BASE_DESTRUCTION_CHANCE_PERCENT + getWateringCount());
        if (destroyed) {
            removeFrom(location);
        }
        return actor + " waters parsnip and restores " + WATER_HEAL_AMOUNT + " HP. "
                + (destroyed ? "The plant is damaged and leaves dirt." : "The plant survives.");
    }

    @Override
    protected String harvestAt(Actor actor, Location location) {
        actor.getInventory().add(new Parsnip("Parsnip", 'p'));
        removeFrom(location);
        String result = actor + " harvests one parsnip. The plant leaves dirt.";
        // Preserve the original harvest's chance of producing another plant,
        // without overwriting existing crops or replanting the removed tile.
        if (random.nextInt(100) < REPLANT_CHANCE_PERCENT) {
            List<Location> candidates = location.map().locationOf(actor).getNearbyLocations(1).stream()
                    .filter(candidate -> candidate != location)
                    .filter(candidate -> candidate.getGround() instanceof Dirt)
                    .toList();
            if (!candidates.isEmpty()) {
                candidates.get(random.nextInt(candidates.size())).setGround(new ParsnipCrop(random));
                result += " A new parsnip appears on nearby dirt.";
            }
        }
        return result;
    }
}
