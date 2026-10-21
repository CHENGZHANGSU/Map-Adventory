package game.products.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A concrete implementation of IntrinsicWeapon for monsters' natural attacks.
 */
public class NaturalWeapon extends IntrinsicWeapon {
    public NaturalWeapon(int damage, String verb, int hitRate, String name) {
        super(damage, verb, hitRate, name);
    }
}