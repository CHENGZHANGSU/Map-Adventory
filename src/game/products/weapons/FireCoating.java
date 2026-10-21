package game.products.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.effects.TimedHealthEffect;

/**
 * A weapon coating that sets the target burning.
 * Deals 5 damage per turn for 3 turns using TimedHealthEffect.
 */
public class FireCoating implements WeaponCoating {

    private static final int BURN_DAMAGE_PER_TURN = 5;
    private static final int BURN_DURATION = 3;

    /**
     * Applies burning status effect to the target actor.
     * Triggers on every attack regardless of whether the physical strike hit.
     *
     * @param attacker the actor who performed the attack
     * @param target   the target actor of the attack
     * @param map      the map on which the attack was executed
     * @param isHit    whether the primary weapon strike landed
     * @return description of the fire coating effect
     */
    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map, boolean isHit) {
        if (target == null) {
            return "";
        }

        // Applies ongoing damage status (-5 HP for 3 turns)
        target.addStatus(new TimedHealthEffect(-BURN_DAMAGE_PER_TURN, BURN_DURATION));
        return String.format("%s catches fire! Taking %d burn damage per turn for %d turns.",
                target, BURN_DAMAGE_PER_TURN, BURN_DURATION);
    }

    @Override
    public String getName() {
        return "Fire";
    }
}
