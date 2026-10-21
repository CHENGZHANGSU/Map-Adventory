package game.terrains;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.TillAction;
import game.actions.WaterAction;
import game.interfaces.Tillable;
import game.interfaces.Waterable;
import game.tools.ToolAbility;
import game.tools.ToolAccess;

/**
 * This is a class representing dirt on the ground.
 * It is a sub-class inherited from Ground from engine package, implementing Tillable, Waterable interface.
 *
 * @author Su Chengzhang
 */
public class Dirt extends Ground implements Tillable, Waterable {

    /**
     * This non-parameterized constructor used to instantiate a Dirt object.
     */
    public Dirt()
    {
        super('.', "Dirt");
    }

    /**
     * This method used to determine whether the actor can perform actions on Dirt object.
     * @param actor The actor performing an action.
     * @return  Whether the actor with the ability to perform the action as a boolean value.
     */
    public boolean requirementMet(Actor actor)
    {
        return actor.hasStatistic(ActorStatistics.STAMINA)
                && actor.getStatistic(ActorStatistics.STAMINA) >= 2;
    }

    /** Checks watering stamina and a carried watering tool (helper for wateredBy). */
    public boolean canWater(Actor actor) {
        return requirementMet(actor) && ToolAccess.hasCarriedTool(actor, ToolAbility.WATER);
    }

    /**
     * This method used to till a tile by the actor on the game map.
     * @param actor The actor perform till action.
     * @param gameMap   The game map the actor in.
     * @return  The result of the till action by the actor as a string.
     */
    @Override
    public String tiltBy(Actor actor, GameMap gameMap)
    {
        String action = "till the tile";
        Location actorLocation = gameMap.locationOf(actor);
        if (actorLocation.getGround().equals(this))
        {
            if (this.requirementMet(actor) & actor.hasAbility(ToolAbility.TILL))
            {
                actorLocation.setGround(new Soil());
                actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, 2);
                String remainStamina = String.format("(stamina: %d/%d)",
                        actor.getStatistic(ActorStatistics.STAMINA), actor.getMaximumStatistic(ActorStatistics.STAMINA));
                return String.format("%s %s successfully %s.",
                        actor, action, remainStamina);
            }
            else
            {
                return String.format("%s fail to %s due to insufficient stamina or without a Hoe.",
                        actor, action);
            }
        }
        else
        {
            return String.format("%s fail to %s due to the ground is not %s.",
                    actor, action, this);
        }
    }

    /**
     * Waters the dirt under the actor if stamina and a carried tool are available.
     * Failed attempts do not consume stamina or change the ground.
     * @param actor actor attempting to water this dirt
     * @param gameMap map containing the actor and this dirt
     * @return description of the result or the reason watering failed
     */
    @Override
    public String wateredBy(Actor actor, GameMap gameMap)
    {
        String action = "water the dirt";
        Location actorLocation = gameMap.locationOf(actor);
        if (actorLocation.getGround().equals(this))
        {
            if (this.canWater(actor))
            {
                actorLocation.setGround(new Puddle());
                actor.modifyStatistic(ActorStatistics.STAMINA, StatisticOperations.DECREASE, 2);
                String remainStamina = String.format("(stamina: %d/%d)",
                        actor.getStatistic(ActorStatistics.STAMINA), actor.getMaximumStatistic(ActorStatistics.STAMINA));
                return String.format("%s %s successfully %s.",
                        actor, action,remainStamina);
            }
            else
            {
                return String.format("%s fail to %s due to insufficient stamina or without a Watering Can.",
                        actor, action);
            }
        }
        else
        {
            return String.format("%s fail to %s due to the ground is not %s.",
                    actor, action, this);
        }
    }

    /**
     * This method return the list of actions can be perform on a Dirt object.
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return  The ActionList object.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction)
    {
        if (direction.isEmpty())
        {
            ActionList actionList = new ActionList();
            actionList.add(new TillAction(this));
            actionList.add(new WaterAction(this));
            return actionList;
        }
        return new ActionList();
    }

}

