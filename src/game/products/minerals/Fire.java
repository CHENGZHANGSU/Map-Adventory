package game.products.minerals;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.ActionList;
import game.interfaces.Coatable;
import game.actions.CoatWeaponAction;
import game.products.weapons.FireCoating;

/**
 * An immovable environmental hazard that burns actors standing on it.
 * Fire remains on the ground for its configured lifespan. An actor
 * standing on fire receives a burn status, which continues damaging the
 * actor for the configured burn duration.
 *
 * @author Zizhuo Wang
 */
public class Fire extends Item {

    private int lifespan;
    private final int burnDuration;
    private final int burnDamage;

    /**
     * Creates a non-portable fire hazard.
     *
     * @param lifespan number of turns that the fire remains on the ground
     * @param burnDuration number of turns an actor remains burned
     * @param burnDamage damage dealt per burning turn
     */
    public Fire(int lifespan, int burnDuration, int burnDamage) {
        super("Fire", 'v');

        if (lifespan <= 0) {
            throw new IllegalArgumentException(
                    "Fire lifespan must be positive.");
        }

        if (burnDuration <= 0) {
            throw new IllegalArgumentException(
                    "Burn duration must be positive.");
        }

        if (burnDamage < 0) {
            throw new IllegalArgumentException(
                    "Burn damage cannot be negative.");
        }

        this.lifespan = lifespan;
        this.burnDuration = burnDuration;
        this.burnDamage = burnDamage;
        makeNonPortable();
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            applyBurningStatus(actor);
        }

        lifespan--;

        if (lifespan <= 0) {
            location.removeItem(this);
        }
    }

    private void applyBurningStatus(Actor actor) {
        BurningStatus burningStatus = actor.statusesOf(BurningStatus.class)
                .stream()
                .findFirst()
                .orElse(null);

        if (burningStatus == null) {
            actor.addStatus(new BurningStatus(burnDuration, burnDamage));
        } else {
            burningStatus.refresh();
        }
    }

    /**
     * Generates allowable actions for an actor standing on or adjacent to this fire.
     * Scans the actor's inventory for coatable weapons to provide a fire coating action.
     *
     * @param otherActor the actor interacting with the fire
     * @param location the location of the fire
     * @return an ActionList containing available actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location) {
        ActionList actions = super.allowableActions(otherActor, location);

        // Scan the adjacent actor's inventory for coatable weapons
        for (Item item : otherActor.getInventory().getItems()) {
            if (item instanceof Coatable) {
                // Fire is an environmental source and is not consumed, so pass null
                actions.add(new CoatWeaponAction((Coatable) item, new FireCoating(), null));
            }
        }

        return actions;
    }

    /**
     * Status effect representing ongoing fire damage.
     */
    private final class BurningStatus implements Status {

        private int remainingTurns;

        private final int damagePerTurn;

        private BurningStatus(int duration, int damagePerTurn) {
            this.remainingTurns = duration;
            this.damagePerTurn = damagePerTurn;
        }

        private void refresh() {
            remainingTurns = burnDuration;
        }

        @Override
        public void tickStatus(
                GameEntity currentEntity,
                Location location) {

            if (currentEntity instanceof Actor actor) {
                actor.hurt(damagePerTurn);
            }

            remainingTurns--;
        }

        @Override
        public boolean isStatusActive() {
            return remainingTurns > 0;
        }
    }
}
