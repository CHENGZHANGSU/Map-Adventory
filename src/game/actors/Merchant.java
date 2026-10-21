package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.BuyAction;
import game.interfaces.Buyable;
import game.interfaces.Sellable;
import game.interfaces.Tradable;
import game.interfaces.Wanderable;
import game.products.crops.*;
import game.products.weapons.Sword;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Merchant extends Actor implements Wanderable, Tradable
{
    private final Random rand = new Random();
//    private final Display display = new Display();


    public Merchant(String name)
    {
        super("Merchant " + name, '$', 100);
        this.getInventory().add(new Sword());
        this.getInventory().add(new ParsnipCrop());
        this.getInventory().add(new TomatoCrop());
    }

    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    @Override
    public boolean requirementMet(Actor actor)
    {
        return !actor.getInventory().getItems().isEmpty();
    }


    /**
     * This method used to determine whether the actor met the requirement to take certain actions.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    protected boolean requirementMet(Actor actor, Buyable item)
    {
        return actor.getStatistic(Prop.COIN) >= ((Item) item).getStatistic(Prop.COIN);
    }

    /**
     * This method used to trade the items between the merchant and farmer.
     * @param actor The farmer as an Actor object.
     * @param gameMap   The game map as a GameMap object.
     * @param item The item involved in the trade.
     * @return  The result of trading as a string.
     */
    @Override
    public String tradeBy(Actor actor, GameMap gameMap, Sellable item)
    {
        String action = String.format("Sell a %s", item.toString().toLowerCase());
        // If the farmer has at least 10 coins
        if (actor.getInventory().getItems().contains((Item) item))
        {
            // pay the coins to the farmer
            actor.modifyStatistic(Prop.COIN, StatisticOperations.INCREASE, ((Item) item).getStatistic(Prop.COIN) );
            // remove the sold item
            actor.getInventory().remove((Item) item);
            // the effects by merchant
            String res = item.soldBy(actor, this, gameMap);

            return String.format("%s %s", actor, res);
        }
        return String.format("%s fail due to such product not found.", action);
    }


    /**
     * This method used to trade the items between the merchant and farmer.
     * @param actor The farmer or merchant as an Actor object.
     * @param gameMap   The game map as a GameMap object.
     * @param item The item involved in the trade.
     * @return  The result of trading as a string.
     */
    @Override
    public String tradeBy(Actor actor, GameMap gameMap, Buyable item)
    {
        String action = String.format("Purchase a %s", item.toString().toLowerCase());
        if (!this.getInventory().getItems().contains((Item) item)) {
            return action + " failed: this product is no longer stocked.";
        }
        if (this.requirementMet(actor, item))
        {
            Buyable purchase = item.newPurchase();
            // pay the coins to the farmer
            actor.modifyStatistic(Prop.COIN, StatisticOperations.DECREASE, ((Item) item).getStatistic(Prop.COIN));
            // add the bought item
            actor.getInventory().add((Item) purchase);
            // the effects by merchant
            String res = purchase.boughtBy(actor, this, gameMap);

            return String.format("%s %s", actor, res);
        }
        return String.format("%s fail due to insufficient coins", action);
    }


    /**
     * This is a method used to control the wandering behavior of merchant actors.
     * @param actor Actor object wandering.
     * @param location Location of the actor.
     * @return Wander Action of the Merchant object.
     */
    @Override
    public Action wander(Actor actor, Location location)
    {
        List<Action> valid = new ArrayList<>();
        for (Exit exit: location.getExits())
        {
            Location destination = exit.getDestination();
            Action movable = destination.getMoveAction(actor, exit.getName(), exit.getHotKey());
            if (movable != null)
            {
                valid.add(movable);
            }
        }

        if (!valid.isEmpty())
        {
            return valid.get(rand.nextInt(valid.size()));
        }
        return null;
    }

    /**
     * This method uses for a merchant actor to perform one round of actions.
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return  An Action object.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {

        display.println(this.merchantInfo());

        if (!this.isConscious()) {
            System.out.println(this.unconscious(map));;
            return new DoNothingAction();
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        // chicken has 50% wander around in each turn
        if (rand.nextDouble() > 0.5)
        {
            Action move = this.wander(this, map.locationOf(this));
            if (move != null)
            {
                return move;
            }
        }
        return new DoNothingAction();
    }

    /**
     * Returns a new collection of the Actions that the farmer can do to the
     * current merchant Actor.
     *
     * @param otherActor the Actor that might be performing buy action
     * @param direction String representing the direction of the other Actor
     * @param map current GameMap
     * @return A collection of Actions.
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map)
    {
        ActionList actions = new ActionList();
        Player farmer = map.locationOf(otherActor).getActorAs(Player.class);
        if (farmer != null)
        {
            for (Buyable item: this.getInventory().getItemsAs(Buyable.class))
            {
                actions.add(new BuyAction(this, item));
            }
        }
        return actions;
    }

    /**
     * This method conclude the farmer object information.
     * @return  Merchant object information as a string.
     */
    public String merchantInfo()
    {
        String inventoryRes = this.getInventory().getItems().toString();
        int merchantHp = this.getStatistic(ActorStatistics.HEALTH);
        return String.format("%s\nHP: %d\nInventory: %s",
                super.name, merchantHp, inventoryRes);
    }
}
