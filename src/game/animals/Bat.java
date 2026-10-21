package game.animals;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.products.crops.Strawberry;
import game.products.weapons.NaturalWeapon;

import java.util.ArrayList;
import java.util.List;

/**
 * A hostile bat that has 50 HP and attacks for 35 damage with a 90% hit rate.
 * Drops a never-rotten strawberry on an adjacent tile when attacked.
 */
public class Bat extends HostileCreature {

    public Bat() {
        super("Bat", 'B', 50);
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        // Deals 35 damage with a 90% chance to hit as per spec
        return new NaturalWeapon(35, "bites", 90, "fangs");
    }

    @Override
    public void onAttacked(Location location, boolean isKilled, Actor attacker, Weapon weapon) {
        // Drop a never-rotten strawberry upon being attacked
        List<Location> adjacentTiles = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            adjacentTiles.add(exit.getDestination());
        }

        if (!adjacentTiles.isEmpty()) {
            Location dropLoc = adjacentTiles.get(rand.nextInt(adjacentTiles.size()));
            dropLoc.addItem(new Strawberry(false));
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
