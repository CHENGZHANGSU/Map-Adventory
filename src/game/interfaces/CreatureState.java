package game.interfaces;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.SporeBeast;

/**
 * Defines the turn behaviour supplied by one SporeBeast state.
 */
public interface CreatureState {

    /**
     * Creates the action performed by the creature while in this state.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     * @return the action for this turn
     */
    Action createTurnAction(SporeBeast creature, GameMap map);

    /**
     * Core framework method: Determines the next state to transition to for the next turn.
     * By default, it returns itself (no transition). Specific states can override this
     * method to implement deterministic transition conditions.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     * @return the next state
     */
    default CreatureState getNextState(SporeBeast creature, GameMap map) {
        return this;
    }

    /**
     * Core framework method: Triggers a unique effect at the exact moment the state changes.
     * Satisfies REQ5: "cause something to happen at the moment the state changes".
     * By default, nothing happens. Other states can override this.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     */
    default void onEnter(SporeBeast creature, GameMap map) {
        // Default: do nothing
    }
}