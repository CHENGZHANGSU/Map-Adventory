package game.animals;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Wanderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class stores all the information of the animal object.
 * It is a sub-class inherited from Actor class, implements Wanderable interface.
 *
 * @author Su Chengzhang
 */
public abstract class Animal extends Actor implements Wanderable
{
    public final Random rand = new Random();
    public final Display display = new Display();

    public Animal(String name, char displayChar, int hitPoints)
    {
        super(name, displayChar, hitPoints);
    }

    /**
     * This is a method used to control the wandering behavior of animal actors.
     * @param actor Actor object wandering.
     * @param location Location of the actor.
     * @return Wander Action of the animal object.
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
     * This method uses for an animal actor to perform one round of actions.
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return  An Action object.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!map.contains(this)) {
            return new DoNothingAction();
        }
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }
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
}
