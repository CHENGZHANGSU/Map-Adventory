package game.terrains;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.products.crops.Strawberry;

import java.util.List;
import java.util.Random;

/** A repeat-harvest crop whose survival improves with watering. */
public class StrawberryCrop extends WaterableCrop {
    private static final int FRUIT_COUNT = 2;
    private static final int ROTTEN_CHANCE_PERCENT = 50;
    private static final int BASE_DESTRUCTION_CHANCE_PERCENT = 50;

    /** Creates a strawberry plant with no previous waterings. */
    public StrawberryCrop() {
        this(new Random());
    }

    /**
     * Accepts a random source so probability outcomes can be tested.
     * @param random source for fruit locations, freshness and plant damage
     */
    public StrawberryCrop(Random random) {
        super('S', "Strawberry crop", 1, 1, random);
    }

    private Strawberry produce() {
        return new Strawberry(random.nextInt(100) < ROTTEN_CHANCE_PERCENT);
    }

    @Override
    protected String waterAt(Actor actor, Location location) {
        List<Location> neighbours = location.getNearbyLocations(1);
        if (neighbours.isEmpty()) {
            return actor + " waters " + this + ", but there are no adjacent tiles for fruit.";
        }
        for (int i = 0; i < FRUIT_COUNT; i++) {
            Location destination = neighbours.get(random.nextInt(neighbours.size()));
            destination.addItem(produce());
        }
        return actor + " waters " + this + ": " + FRUIT_COUNT + " strawberries fall on adjacent tiles.";
    }

    @Override
    protected String harvestAt(Actor actor, Location location) {
        for (int i = 0; i < FRUIT_COUNT; i++) {
            actor.getInventory().add(produce());
        }
        boolean destroyed = random.nextInt(100)
                < Math.max(0, BASE_DESTRUCTION_CHANCE_PERCENT - getWateringCount());
        if (destroyed) {
            removeFrom(location);
        }
        return actor + " harvests " + FRUIT_COUNT + " strawberries. "
                + (destroyed ? "The plant is damaged and leaves dirt." : "The plant survives.");
    }
}
