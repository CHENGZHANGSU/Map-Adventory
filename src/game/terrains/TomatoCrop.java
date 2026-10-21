package game.terrains;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import game.products.crops.Tomato;
import game.util.Statistics;

import java.util.Random;

/**
 * The original tomato crop, protected from cauliflower flooding like other crops.
 * @author Su Chengzhang (original implementation)
 */
public class TomatoCrop extends Crop {
    /** Creates a tomato plant with its original harvesting cost and effects. */
    public TomatoCrop() {
        super('T', "Tomato crop", 2, new Random());
    }

    @Override
    protected String harvestAt(Actor actor, Location location) {
        actor.getInventory().add(new Tomato("Tomato", 'o'));
        actor.getInventory().add(new Tomato("Tomato", 'o'));
        Statistics.increaseMaximum(actor, ActorStatistics.STAMINA, 1);
        removeFrom(location);
        return actor + " harvests two tomatoes and gains 1 maximum stamina. The plant leaves dirt.";
    }
}
