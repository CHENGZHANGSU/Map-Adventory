package game.animals;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.actors.Farmer;
import game.interfaces.Hostile;

/**
 * Abstract base class for all hostile creatures in the mines.
 * Implements Hostile to become a valid target for the Farmer's sword.
 * Capable of actively seeking and attacking the Farmer if adjacent.
 */
public abstract class HostileCreature extends Animal implements Hostile {

    /**
     * Constructor for HostileCreature.
     *
     * @param name        the creature's name
     * @param displayChar the character representing the creature on the map
     * @param hitPoints   the creature's starting health points
     */
    public HostileCreature(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }

    /**
     * Determines the creature's action for the current turn.
     * Prioritizes attacking an adjacent Farmer. If no Farmer is present,
     * falls back to the default Animal wandering behavior.
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn.
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the Action to be performed
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!map.contains(this)) {
            return new DoNothingAction();
        }
        if (!isConscious()) {
            return super.playTurn(actions, lastAction, map, display);
        }
        Location here = map.locationOf(this);

        // 1. Scan adjacent tiles for a Farmer
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor() && destination.getActor() instanceof Farmer) {
                // If a Farmer is adjacent, immediately attack using the creature's intrinsic weapon
                return new AttackAction(this.getIntrinsicWeapon(), destination.getActor());
            }
        }

        // 2. If no Farmer is adjacent, fallback to wandering logic defined in Animal
        return super.playTurn(actions, lastAction, map, display);
    }

    /**
     * Generates allowable actions that another actor can perform on this creature.
     * This method implements double dispatch to allow unarmed combat.
     *
     * @param otherActor the actor performing actions upon this creature
     * @param direction  the direction of this creature from the other actor
     * @param map        the map containing this creature
     * @return an ActionList containing available actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = super.allowableActions(otherActor, direction, map);


        if (otherActor instanceof Farmer) {
            actions.add(new AttackAction(otherActor.getIntrinsicWeapon(), this));
        }

        return actions;
    }
}
