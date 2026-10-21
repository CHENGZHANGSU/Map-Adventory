package game.products.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Strategy interface defining weapon coating behaviors.
 * Any coating applied to a weapon must implement this interface.
 */
public interface WeaponCoating {

    /**
     * Executes the coating's effect upon an attack.
     * Note: Per requirements, the coating effect triggers on every attack,
     * including attacks that miss.
     *
     * @param attacker the actor who performed the attack
     * @param target   the target actor of the attack
     * @param map      the map on which the attack was executed
     * @param isHit    whether the primary weapon strike successfully landed
     * @return description of what the coating did
     */
    String applyEffect(Actor attacker, Actor target, GameMap map, boolean isHit);

    /**
     * Returns the name of the coating.
     *
     * @return the coating name (e.g. "Fire", "Charged", "Melon")
     */
    String getName();
}
