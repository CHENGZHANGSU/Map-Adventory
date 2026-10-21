package game.products.weapons;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * A weapon coating derived from melon produce.
 * Explodes on impact, dealing 15 damage to every actor adjacent to the target,
 * including the farmer who is standing next to the target.
 */
public class MelonCoating implements WeaponCoating {

    private static final int EXPLOSION_DAMAGE = 15;

    /**
     * Triggers an explosion centered around the target's location,
     * hurting all actors on adjacent tiles (including the attacker).
     *
     * @param attacker the actor who performed the attack
     * @param target   the target actor of the attack
     * @param map      the map on which the attack was executed
     * @param isHit    whether the primary weapon strike landed
     * @return description of the explosion and affected victims
     */
    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map, boolean isHit) {
        if (target == null || !map.contains(target)) {
            return "";
        }

        Location targetLocation = map.locationOf(target);
        List<String> affectedVictims = new ArrayList<>();

        // Explodes outward to all tiles adjacent to the target
        for (Exit exit : targetLocation.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor victim = destination.getActor();
                victim.hurt(EXPLOSION_DAMAGE);
                affectedVictims.add(victim.toString());
            }
        }

        if (!affectedVictims.isEmpty()) {
            return String.format("The melon coating explodes! Dealing %d damage to adjacent actors: %s.",
                    EXPLOSION_DAMAGE, String.join(", ", affectedVictims));
        }

        return "The melon coating explodes, but no one was standing nearby.";
    }

    @Override
    public String getName() {
        return "Melon";
    }
}
