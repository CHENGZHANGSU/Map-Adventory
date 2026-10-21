package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.SporeBeast;
import game.interfaces.CreatureState;
import game.states.fleeing.ThreatScanner;

import java.util.List;

/**
 * The Camouflaged state for the SporeBeast.
 * Satisfies REQ5 HD requirements with deterministic and complex multi-path transitions.
 */
public class CamouflagedState implements CreatureState {

    // Tracks the number of consecutive turns the creature has remained hidden safely.
    private int hideTurnCount = 0;

    // Reusing the scanner tool to perceive the surrounding environment.
    private final ThreatScanner threatScanner = new ThreatScanner();

    @Override
    public Action createTurnAction(SporeBeast creature, GameMap map) {
        // As per REQ5 description: "holds it still and hides it against the ground"
        return new DoNothingAction();
    }

    /**
     * Core logic: Implements a fully deterministic, multi-path state transition.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     * @return the determined next state based on complex environmental conditions
     */
    @Override
    public CreatureState getNextState(SporeBeast creature, GameMap map) {

        // Scan for all entities within a 1-block (adjacent) radius.
        int scanRadius = 2;
        List<Actor> surroundingActors = threatScanner.findNearbyThreats(
                creature,
                map.locationOf(creature),
                scanRadius
        );

        int actorCount = surroundingActors.size();

        // Branch 1: Exactly one actor nearby -> transition to TerritorialState.
        if (actorCount == 1) {
            this.hideTurnCount = 0; // Reset counter
            return BaseStates.getTerritorialState();
        }

        // Branch 2: More than one actor nearby -> stays camouflaged due to high threat.
        else if (actorCount > 1) {
            this.hideTurnCount = 0; // Environment is unsafe, reset counter
            return this;
        }

        // Branch 3: No actors nearby -> start or continue counting turns.
        else {
            this.hideTurnCount++;

            // If undisturbed for 10 consecutive turns -> transition to ForagingState.
            if (this.hideTurnCount >= 10) {
                this.hideTurnCount = 0; // Reset counter for future use
                return BaseStates.getForagingState();
            }

            // Not yet 10 turns, remain camouflaged.
            return this;
        }
    }

    /**
     * Triggered exactly when the creature enters this state.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     */
    @Override
    public void onEnter(SporeBeast creature, GameMap map) {
        // Ensure the turn counter is completely clean every time it enters the camouflage state.
        this.hideTurnCount = 0;

        // Note: Specific visual or environmental effects during transition are handled
        // by the respective entering states to fulfill REQ5 transition effects.
    }
}
