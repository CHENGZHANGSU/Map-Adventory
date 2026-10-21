package game.animals;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import edu.monash.fit2099.engine.weapons.Weapon;
import edu.monash.fit2099.engine.displays.Display;
import game.products.weapons.NaturalWeapon;

/**
 * A hostile cave ghost that has 250 HP and attacks for 25 damage with a 75% hit rate.
 * Explodes upon death, dealing 50 damage to all adjacent actors.
 */
public class CaveGhost extends HostileCreature {

    private static final int EXPLOSION_DAMAGE = 50;

    public CaveGhost() {
        super("Cave Ghost", 'G', 250);
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        // Deals 25 damage with a 75% chance to hit as per spec
        return new NaturalWeapon(25, "haunts", 75, "chill");
    }

    @Override
    public void onAttacked(Location location, boolean isKilled, Actor attacker, Weapon weapon) {
        if (isKilled) {
            // Print visual feedback for the explosion
            Display display = new Display();
            display.println("BOOM! Cave Ghost explodes upon death, dealing 50 AOE damage!");

            // Apply 50 AOE damage to all adjacent actors
            for (Exit exit : location.getExits()) {
                Location dest = exit.getDestination();
                if (dest.containsAnActor()) {
                    Actor victim = dest.getActor();
                    victim.hurt(EXPLOSION_DAMAGE);
                }
            }

            // Remove the actor from the map upon death to prevent the zombie bug
            try {
                location.map().removeActor(this);
            } catch (Exception e) {
                // Silently handle exception
            }
        }
    }
}