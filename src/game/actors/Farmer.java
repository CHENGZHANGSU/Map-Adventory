package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.products.animalproducts.Milk;
import game.products.minerals.Diamond;
import game.products.weapons.NaturalWeapon;
import game.products.weapons.Sword;
import game.tools.Hoe;
import game.tools.Pickaxe;
import game.tools.ToolAbility;
import game.tools.WateringCan;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;


/**
 * This class stores all the properties and behaviours of a farmer object.
 *
 * <p>It is a subclass of the Player class.</p>
 *
 * @author Su Chengzhang
 */
public class Farmer extends Player {

    /**
     * Creates a farmer with the specified name.
     *
     * @param name the farmer's name
     */
    public Farmer(String name) {
        super("Farmer " + name, '@', 100);

        this.addNewStatistic(
                ActorStatistics.STAMINA,
                new BaseStatistic(20));

        this.addNewStatistic(
                Prop.COIN,
                new BaseStatistic(10000));

        // Initialise the player's items.
        this.getInventory().add(new Hoe("Hoe", 'h'));
        this.enableAbility(ToolAbility.TILL);

        this.getInventory().add(new WateringCan("Watering Can", 'w'));
        this.enableAbility(ToolAbility.WATER);

        // REQ2 mining tool from your branch.
        this.getInventory().add(new Pickaxe());

        // Items from the main branch.
        this.getInventory().add(new Diamond());
        this.getInventory().add(new Milk());
        this.getInventory().add(new Sword());
    }

    /**
     * Allows the farmer to perform one turn of actions.
     *
     * @param actions available actions
     * @param lastAction the action taken during the previous turn
     * @param map the map containing the farmer
     * @param display the display used for input and output
     * @return the action selected by the farmer
     */
    @Override
    public Action playTurn(
            ActionList actions,
            Action lastAction,
            GameMap map,
            Display display) {

        if (!this.isConscious()) {
            System.out.println(this.unconscious(map));
            return new DoNothingAction();
        }

        // Handle multi-turn actions.
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // Restore one stamina point before displaying the menu.
        this.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                1);

        System.out.println(this.farmerInfo());

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * Returns information about the farmer.
     *
     * @return the farmer's health, stamina, coins, and inventory
     */
    public String farmerInfo() {
        String inventoryResult = this.getInventory().getItems().toString();
        int farmerHealth = this.getStatistic(ActorStatistics.HEALTH);
        int farmerStamina = this.getStatistic(ActorStatistics.STAMINA);
        int farmerCoins = this.getStatistic(Prop.COIN);

        return String.format(
                "%s%nHP: %d%nSP: %d%nCoins: %d%nInventory: %s%n",
                super.name,
                farmerHealth,
                farmerStamina,
                farmerCoins,
                inventoryResult);
    }

    /**
     * Creates and returns the farmer's intrinsic weapon (fists).
     * This defines the default unarmed combat capability.
     *
     * @return an IntrinsicWeapon representing the farmer's fists.
     */
    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return new NaturalWeapon(5, "punches", 80, "fist");
    }
}