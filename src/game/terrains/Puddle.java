package game.terrains;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * This class contain all the properties of a Puddle object.
 * It is a sub-class inherited from ChargeableGround.
 *
 * @author Su Chengzhang
 */
public class Puddle extends ChargeableGround {

    private static final int SHOCK_DAMAGE = 20;

    /**
     * This is a non-parameterized constructor used to instantiate a Puddle object.
     */
    public Puddle() {
        super('~', "Puddle");
    }

    /**
     * Shocks an actor standing on this puddle while it is charged.
     *
     * @param location location containing this puddle
     */
    @Override
    public void tick(Location location) {
        if (isCharged() && location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.hurt(SHOCK_DAMAGE);
        }
    }
}
