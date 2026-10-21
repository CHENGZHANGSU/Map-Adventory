package game.interfaces;

import game.products.weapons.WeaponCoating;

/**
 * Interface representing items or weapons that can receive a temporary or permanent coating.
 * Fulfills the open-closed design requirement for future coatable equipment.
 */
public interface Coatable {

    /**
     * Applies a new coating to this weapon, replacing any existing coating.
     *
     * @param coating the WeaponCoating strategy to apply
     */
    void setCoating(WeaponCoating coating);

    /**
     * Retrieves the current coating applied on the weapon.
     *
     * @return the active WeaponCoating, or null if none
     */
    WeaponCoating getCoating();

    /**
     * Checks if this weapon currently has an active coating.
     *
     * @return true if a coating is present, false otherwise
     */
    boolean hasCoating();
}
