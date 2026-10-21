package game.products.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A weapon coating derived from charged ground.
 * Deals 30 extra damage to the target on every attack.
 */
public class ChargedCoating implements WeaponCoating {

    private static final int BONUS_DAMAGE = 30;

    /**
     * Shocks the target for 30 additional electrical damage.
     *
     * @param attacker the actor who performed the attack
     * @param target   the target actor of the attack
     * @param map      the map on which the attack was executed
     * @param isHit    whether the primary weapon strike landed
     * @return description of the electrical discharge
     */
    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map, boolean isHit) {
        if (target == null) {
            return "";
        }

        target.hurt(BONUS_DAMAGE);
        return String.format("%s discharges electricity into %s for %d bonus damage.",
                attacker, target, BONUS_DAMAGE);
    }

    @Override
    public String getName() {
        return "Charged";
    }
}
