package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.interfaces.Hostile;

/**
 * This class stores all entities and behaviors needed for attack action to be performed.
 * It is a sub-class inherited from Action super class.
 *
 * @author Su Chengzhang
 */
public class AttackAction extends Action
{
    private final Weapon weapon;
    private final Actor otherActor;

    /**
     * This method uses to instantiate a AttackAction object.
     *
     * @param weapon    The weapon object uses to attack.
     * @param otherActor The target.
     */
    public AttackAction(Weapon weapon, Actor otherActor)
    {
        this.weapon = weapon;
        this.otherActor = otherActor;
    }

    /**
     * This method used to execute the attack action of the actor.
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return  Execution result of AttackAction as a string.
     */
    @Override
    public String execute(Actor actor, GameMap map)
    {
        if (!map.contains(actor) || !actor.isConscious()
                || !map.contains(otherActor) || !otherActor.isConscious()) {
            return actor + " cannot attack: attacker or target is no longer active.";
        }
        String result = weapon.attack(actor, otherActor, map);
        // Every weapon, including fists, uses the same reaction/death path.
        if (map.contains(otherActor) && otherActor instanceof Hostile hostile) {
            hostile.onAttacked(map.locationOf(otherActor), !otherActor.isConscious(), actor, weapon);
        }
        if (map.contains(otherActor) && !otherActor.isConscious()) {
            result += "\n" + otherActor.unconscious(actor, map);
        }
        if (map.contains(actor) && !actor.isConscious()) {
            result += "\n" + actor.unconscious(map);
        }
        return result;
    }

    /**
     * This method used to describe the attack action with the weapon of the attackable object in a menu.
     * @param actor The actor performing the action.
     * @return  Menu description of the attack action of the attackable object as a string.
     */
    @Override
    public String menuDescription(Actor actor)
    {
        return String.format("%s attack %s by %s",
                actor, this.otherActor.toString(), this.weapon.toString());
    }
}
