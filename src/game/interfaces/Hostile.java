package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Interface identifying hostile entities.
 * Defines behaviors triggered when the entity is attacked.
 */
public interface Hostile {

    /**
     * Triggered immediately after this hostile entity is attacked by a weapon.
     * Provides full combat context for highly extensible drop/reaction mechanics.
     *
     * @param location the location where the hostile entity is standing
     * @param isKilled true if the attack dropped the entity's HP to 0 or below
     * @param attacker the actor who performed the attack
     * @param weapon   the weapon used for the attack
     */
    void onAttacked(Location location, boolean isKilled, Actor attacker, Weapon weapon);
}

