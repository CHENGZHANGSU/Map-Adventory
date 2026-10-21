package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Harvestable;

/**
 * This class store all properties and behaviours of a HarvestAction object.
 * It is a sub-class inherited from Action class in engine package.
 *
 * @author Su Chengzhang
 */
public class HarvestAction extends Action
{
    private final Harvestable harvestable;
    private final String exitName;

    /**
     * This method uses to instantiate a HarvestAction object.
     * @param harvestable   Harvestable item objet.
     * @param exitName     Direction of the facility.
     */
    public HarvestAction(Harvestable harvestable, String exitName)
    {
        this.harvestable = harvestable;
        this.exitName = exitName;
    }

    /**
     * This method used to execute the harvest action of the harvestable item.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of HarvestAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        return this.harvestable.harvestBy(actor, map);
    }

    /**
     * This method used to describe the harvest action of the harvestable item in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the harvest action of the harvestable as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        if (this.exitName.isEmpty())
        {
            return String.format("%s harvest a %s",
                    actor, this.harvestable.toString().toLowerCase());
        }
        return String.format("%s harvest a %s at %s",
                actor, this.harvestable.toString().toLowerCase(), this.exitName);
    }
}
