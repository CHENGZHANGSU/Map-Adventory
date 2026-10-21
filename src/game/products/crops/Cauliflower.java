package game.products.crops;

import edu.monash.fit2099.engine.actors.Actor;

import java.util.Objects;
import java.util.Random;

/** Eating a cauliflower may produce a new, independently consumable item. */
public class Cauliflower extends ConsumableProduce {
    private static final int HEAL_AMOUNT = 25;
    private static final int REPLACEMENT_CHANCE_PERCENT = 50;
    private final Random random;

    /** Creates an edible cauliflower. */
    public Cauliflower() {
        this(new Random());
    }

    /**
     * Creates food with a supplied random source.
     * @param random source for the replacement-item chance
     */
    public Cauliflower(Random random) {
        super("Cauliflower", 'f');
        this.random = Objects.requireNonNull(random);
    }

    @Override
    protected String consume(Actor actor) {
        actor.heal(HEAL_AMOUNT);
        if (random.nextInt(100) < REPLACEMENT_CHANCE_PERCENT) {
            actor.getInventory().add(new Cauliflower(random));
            return actor + " eats cauliflower, restores " + HEAL_AMOUNT + " HP and receives a new cauliflower.";
        }
        return actor + " eats cauliflower and restores " + HEAL_AMOUNT + " HP.";
    }
}
