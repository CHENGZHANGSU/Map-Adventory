package game.terrains;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.products.crops.Cauliflower;

import java.util.Random;

/** A crop that needs five waterings before a harvest can be attempted. */
public class CauliflowerCrop extends WaterableCrop {
    private static final int REQUIRED_WATERINGS = 5;
    private static final int FLOOD_CHANCE_PERCENT = 50;
    private static final int HARVEST_SUCCESS_CHANCE_PERCENT = 50;

    /** Creates an unwatered cauliflower plant. */
    public CauliflowerCrop() {
        this(new Random());
    }

    /**
     * Creates a plant with a supplied random source.
     * @param random source for flooding and harvest outcomes
     */
    public CauliflowerCrop(Random random) {
        super('F', "Cauliflower crop", 3, 2, random);
    }

    @Override
    protected String waterAt(Actor actor, Location location) {
        // One roll decides whether ALL surrounding non-crop grounds become puddles.
        if (random.nextInt(100) < FLOOD_CHANCE_PERCENT) {
            for (Location neighbour : location.getNearbyLocations(1)) {
                if (neighbour.getGroundAs(Crop.class) == null) {
                    neighbour.setGround(new Puddle());
                }
            }
            return actor + " waters " + this + ": adjacent non-crop grounds become puddles.";
        }
        return actor + " waters " + this + " (" + getWateringCount() + "/" + REQUIRED_WATERINGS + " waterings).";
    }

    @Override
    protected String harvestFailure() {
        return getWateringCount() < REQUIRED_WATERINGS
                ? "Cannot harvest cauliflower: needs five waterings (currently " + getWateringCount() + ")."
                : null;
    }

    @Override
    protected String harvestAt(Actor actor, Location location) {
        removeFrom(location);
        if (random.nextInt(100) < HARVEST_SUCCESS_CHANCE_PERCENT) {
            actor.getInventory().add(new Cauliflower(random));
            return actor + " harvests one cauliflower. The plant leaves dirt.";
        }
        return actor + " fails to harvest any cauliflower. The plant is removed and leaves dirt.";
    }
}
