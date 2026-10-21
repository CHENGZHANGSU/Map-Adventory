package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.interfaces.Chaseable;
import game.interfaces.CreatureState;
import game.states.BaseStates;
import game.creatures.path.DeterministicPathfinder;

import java.util.ArrayList;
import java.util.List;

/**
 * REQ5 SporeBeast that utilizes a pure State Machine pattern.
 */
public class SporeBeast extends Actor implements Chaseable {

    // Pointer to record the current state
    private CreatureState currentState;
    private List<Location> chasing;

    /**
     * Creates a testable SporeBeast.
     */
    public SporeBeast() {
        super("SporeBeast", '?', 100);
        this.addNewStatistic(ActorStatistics.HEALTH, new BaseStatistic(30));

        this.setIntrinsicWeapon(new IntrinsicWeapon(20, "lash", 60, "spore") {
        });

        this.chasing = new ArrayList<>();
        // Set the initial state to CamouflagedState
        this.currentState = BaseStates.getCamouflagedState();
    }

    /**
     * Moves one legal step along a shortest path to an occupied target's neighbour.
     * @param actor the creature executing this chase
     * @param gameMap the current map
     * @param targetLocation the target's current location
     * @return a movement or waiting message
     */
    @Override
    public String chasedBy(Actor actor, GameMap gameMap, Location targetLocation) {
        if (!this.chasing.isEmpty()) {
            if (!gameMap.isAnActorAt(this.chasing.getFirst()))
            {
                gameMap.moveActor(this, chasing.getFirst());
                this.chasing.clear();
                return String.format("The %s is chasing the %s😝", this, gameMap.getActorAt(targetLocation));
            }
        }
        this.chasing.add(targetLocation);
        return String.format("The %s record the chase to %s.", this, gameMap.getActorAt(targetLocation));
    }

    /**
     * Unified state transition process (State Machine Control Center)
     * Satisfies REQ5: At most one transition per turn, triggering an effect upon entry.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display)
    {

        // Rotten food can reduce health to zero during the engine's status tick.
        if (!isConscious()) {
            display.println(unconscious(map));
            return new DoNothingAction();
        }

        // 1. Ask the current state if a transition is needed based on deterministic conditions
        CreatureState nextState = this.currentState.getNextState(this, map);

        // 2. If the state has changed (Ensures at most one transition per turn)
        if (nextState != this.currentState) {
            display.println(String.format("%s changes state to %s!",
                    this.name, nextState.getClass().getSimpleName()));

            // Update the current state pointer
            this.currentState = nextState;

            // 3. Trigger the entry effect of the new state (Ensures entry effect runs only once)
            this.currentState.onEnter(this, map);
        }

        // 4. Execute and return the standard turn action of the current state
        return this.currentState.createTurnAction(this, map);
    }
}
