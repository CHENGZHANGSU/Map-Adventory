package game.products.crops;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.util.Statistics;
import game.interfaces.Coatable;
import game.actions.CoatWeaponAction;
import game.products.weapons.MelonCoating;

/** A melon permanently increases maximum health without refilling current health. */
public class Melon extends ConsumableProduce {
    private static final int MAXIMUM_HEALTH_INCREASE = 10;

    /** Creates a portable melon that increases maximum HP on consumption. */
    public Melon() {
        super("Melon", 'm');
    }

    @Override
    protected String consume(Actor actor) {
        Statistics.increaseMaximum(actor, ActorStatistics.HEALTH, MAXIMUM_HEALTH_INCREASE);
        return actor + " eats melon: maximum HP increases by " + MAXIMUM_HEALTH_INCREASE + ".";
    }

    /**
     * Generates allowable actions for the owner of this item.
     * Scans the owner's inventory for coatable weapons to provide a coating action.
     *
     * @param owner the actor holding this melon
     * @param map the map containing the actor
     * @return an ActionList containing available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {

        ActionList actions = super.allowableActions(owner, map);

        // Scan the owner's inventory for coatable weapons
        for (Item item : owner.getInventory().getItems()) {
            if (item instanceof Coatable) {
                // Pass this melon as the consumed item for the coating action
                actions.add(new CoatWeaponAction((Coatable) item, new MelonCoating(), this));
            }
        }
        return actions;
    }
}
