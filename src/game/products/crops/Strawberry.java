package game.products.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.effects.TimedHealthEffect;

/** Each strawberry keeps the freshness assigned when it was produced. */
public class Strawberry extends ConsumableProduce {
    private static final int HEAL_PER_TURN = 1;
    private static final int HEAL_DURATION = 2;
    private static final int POISON_DAMAGE_PER_TURN = 1;
    private static final int POISON_DURATION = 3;
    private final boolean rotten;

    /**
     * Creates a strawberry whose freshness does not change on pickup or consumption.
     * @param rotten whether the fruit causes poison instead of healing
     */
    public Strawberry(boolean rotten) {
        super(rotten ? "Rotten strawberry" : "Strawberry", 's');
        this.rotten = rotten;
    }

    /** @return whether this strawberry is rotten */
    public boolean isRotten() {
        return rotten;
    }

    @Override
    protected String consume(Actor actor) {
        actor.addStatus(new TimedHealthEffect(
                rotten ? -POISON_DAMAGE_PER_TURN : HEAL_PER_TURN,
                rotten ? POISON_DURATION : HEAL_DURATION));
        return actor + " eats " + this + (rotten
                ? ": poisoned for " + POISON_DAMAGE_PER_TURN + " damage per turn for " + POISON_DURATION + " turns."
                : ": restores " + HEAL_PER_TURN + " HP per turn for " + HEAL_DURATION + " turns.");
    }
}
