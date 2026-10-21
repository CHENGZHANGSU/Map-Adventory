package game.animals;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.products.weapons.NaturalWeapon;

import java.util.ArrayList;
import java.util.List;

/**
 * A hostile slime that has 150 HP and attacks for 15 damage with a 50% hit rate.
 * Has a 50% chance to spawn another slime on an adjacent tile when attacked.
 */
public class Slime extends HostileCreature {

    public Slime() {
        super("Slime", 's', 150);
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        // Deals 15 damage with a 50% chance to hit as per spec
        return new NaturalWeapon(15, "splats", 50, "goo");
    }

    @Override
    public void onAttacked(Location location, boolean isKilled, Actor attacker, Weapon weapon) {
        // 50% chance to duplicate upon taking damage
        if (rand.nextDouble() <= 0.5) {
            List<Location> validSpawns = new ArrayList<>();
            for (Exit exit : location.getExits()) {
                Location dest = exit.getDestination();
                if (!dest.containsAnActor()) {
                    validSpawns.add(dest);
                }
            }

            if (!validSpawns.isEmpty()) {
                Location spawnLoc = validSpawns.get(rand.nextInt(validSpawns.size()));
                try {
                    spawnLoc.addActor(new Slime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Remove the actor from the map upon death to prevent the zombie bug
        if (isKilled) {
            try {
                location.map().removeActor(this);
            } catch (Exception e) {
                // Silently handle exception
            }
        }
    }
}