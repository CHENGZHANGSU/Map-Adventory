package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Processable;
import game.products.crops.ProcessedProduce;
import game.terrains.facilities.Facility;

/**
 * This class store all properties and behaviours of a ProcessAction object.
 * It is a sub-class inherited from Action class in engine package.
 *
 * @author Su Chengzhang
 */
public class ProcessAction extends Action
{
    private final Processable food;
    private final Facility facility;
    private final String direction;

    /**
     * This method uses to instantiate a ProcessAction object.
     * @param food   Consumable item objet.
     * @param facility The facility used to process the item object.
     * @param direction     Direction of the facility.
     */
    public ProcessAction(Processable food, Facility facility, String direction)
    {
        this.food = food;
        this.facility = facility;
        this.direction = direction;
    }

    /**
     * This method used to execute the process action of the consumable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of ProcessAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (!actor.getInventory().getItems().contains(food.getItem())) {
            return actor + " cannot process " + food.getItem() + ": not in inventory.";
        }
        actor.getInventory().remove((Item) food);
        ProcessedProduce newProduct = facility.process(food);
        actor.getInventory().add(newProduct);
        return actor
                + " processed "
                + food.getItem()
                + " in "
                + facility
                + " into "
                + newProduct
                + ".";
    }

    /**
     * This method used to describe the process action of the processable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the process action of the processable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        String dirDesc = direction.isEmpty() ? "here" : "to the " + direction;
        return actor + " processes " + food.getItem() + " in " + facility + " " + dirDesc;
    }
}
