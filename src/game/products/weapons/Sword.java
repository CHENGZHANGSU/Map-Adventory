package game.products.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.AttackAction;
import game.actors.Farmer;
import game.actors.Player;
import game.actors.Prop;
import game.actors.SporeBeast;
import game.interfaces.Buyable;
import game.interfaces.Coatable;
import game.interfaces.Hostile;
import game.products.Status;

import java.util.Random;

/**
 * A sword weapon that deals 20 damage with an 85% hit rate.
 * Can be purchased from the merchant and coated with elemental/produce coatings.
 * Only valid targets for sword attacks are hostile creatures.
 */
public class Sword extends Item implements Buyable, Weapon, Coatable {

    private static final int BASE_DAMAGE = 20;
    private static final int HIT_RATE = 85;
    private final Random rand = new Random();
    private WeaponCoating coating;

    @Override
    public Sword newPurchase() { return new Sword(); }

    /**
     * Constructor for Sword.
     * Sets buy cost to 3000 and base attack power to 20.
     */
    public Sword() {
        super("Sword", '↑');
        this.addNewStatistic(Prop.COIN, new BaseStatistic(3000));
        this.addNewStatistic(Status.ATTACK, new BaseStatistic(BASE_DAMAGE));
        this.coating = null;
    }

    /**
     * Buys the sword from the merchant with a 50% chance the merchant takes money and gives nothing.
     */
    @Override
    public String boughtBy(Actor buyer, Actor other, GameMap gameMap) {
        String badNews = "❌Oops, the merchant hands over nothing.😟";
        if (rand.nextDouble() > 0.5) {
            buyer.getInventory().remove(this);
            return String.format("purchase the %s successfully. %s",
                    this.toString().toLowerCase(), badNews);
        }
        return String.format("purchase the %s successfully.", this.toString().toLowerCase());
    }

    /**
     * Executes an attack against the target.
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        boolean isHit = rand.nextInt(100) < HIT_RATE;
        StringBuilder result = new StringBuilder();

        if (isHit) {
            target.hurt(BASE_DAMAGE);
            result.append(String.format("%s attacks %s with %s for %d damage.",
                    attacker, target, this, BASE_DAMAGE));
        } else {
            result.append(String.format("%s misses %s.", attacker, target));
        }

        // Coating takes effect on every attack, including missed swings
        if (this.coating != null) {
            String coatingEffect = this.coating.applyEffect(attacker, target, map, isHit);
            if (coatingEffect != null && !coatingEffect.isEmpty()) {
                result.append(" ").append(coatingEffect);
            }
        }

        return result.toString();
    }

    /**
     * Returns allowable actions when this weapon is in inventory.
     * Restricts attacks strictly to targets that implement Hostile.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location) {
        ActionList actions = new ActionList();
        if (otherActor instanceof Hostile) {
            actions.add(new AttackAction(this, otherActor));
        }
        if (otherActor instanceof SporeBeast)
        {
            actions.add(new AttackAction(this, otherActor));
        }
        return actions;
    }

    /** The engine queries carried items each turn; inspect nearby fire here. */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);
        if (!map.contains(owner)) return actions;
        Location here = map.locationOf(owner);
        java.util.List<Location> nearby = new java.util.ArrayList<>();
        nearby.add(here);
        for (var exit : here.getExits()) nearby.add(exit.getDestination());
        for (Location source : nearby) {
            if (source.getItems().stream().anyMatch(item -> item instanceof game.products.minerals.Fire)) {
                actions.add(new game.actions.CoatWeaponAction(this, new FireCoating(), null));
                break;
            }
        }
        return actions;
    }

    @Override
    public void setCoating(WeaponCoating coating) {
        this.coating = coating;
    }

    @Override
    public WeaponCoating getCoating() {
        return this.coating;
    }

    @Override
    public boolean hasCoating() {
        return this.coating != null;
    }

    @Override
    public String toString() {
        if (hasCoating()) {
            return String.format("%s (%s-coated)", super.toString(), this.coating.getName());
        }
        return super.toString();
    }
}
