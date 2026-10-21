package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.actions.ChaseAction;
import game.actors.SporeBeast;
import game.interfaces.CreatureState;
import game.products.trap.SporeTrap;
import game.products.trap.Trap;
import game.states.fleeing.ThreatScanner;

import java.util.List;

/**
 * REQ 5, the creature move to territorial state. Which possible chase or attack the players.
 *
 * @author Su Chengzhang
 */
public class TerritorialState implements CreatureState
{
    // Tracks the number of consecutive turns the creature has remained hidden safely.
    private int hideTurnCount = 0;
    private final ThreatScanner threatScanner = new ThreatScanner();


    /**
     * Creates the action performed by the creature while in this state.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     * @return the action for this turn
     */
    @Override
    public Action createTurnAction(SporeBeast creature, GameMap map)
    {
        if (!map.contains(creature) || !creature.isConscious()) {
            return new DoNothingAction();
        }
        int SCAN_RADIUS = 2;
        int ATTACK_RADIUS = 1;
        List<Actor> threats = threatScanner.findNearbyThreats(creature, map.locationOf(creature), SCAN_RADIUS);
        Location creatureLoc = map.locationOf(creature);
        List<Exit> surroundings = creatureLoc.getExits();
        if (!threats.isEmpty()) // when threat exist nearby 2 units of tiles
        {
            for (Exit exit: surroundings)
            {
                if (exit.getDestination().getItemsAs(Trap.class).isEmpty())
                {
                    exit.getDestination().addItem(new SporeTrap());
                    break;
                }
            }
        }

        // only one people, chase or attack
        if (threats.size() == 1)
        {
            Actor target = threats.getFirst();
            List<Actor> adjacentThreats = threatScanner.findNearbyThreats(creature, creatureLoc, ATTACK_RADIUS);
            if (adjacentThreats.size() == 1)
            {
                return new AttackAction(creature.getIntrinsicWeapon(), target);
            }
            else
            {
                // chase
                return new ChaseAction(creature, map.locationOf(target));
            }
        }
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
        if (!map.contains(creature) || !creature.isConscious()) {
            return this;
        }

        // Scan for all entities within a 1-block (adjacent) radius.
        int scanRadius = 1;
        List<Actor> surroundingActors = threatScanner.findNearbyThreats(
                creature,
                map.locationOf(creature),
                scanRadius
        );

        int actorCount = surroundingActors.size();
        // A threat in chase range also interrupts consecutive quiet turns.
        boolean threatNearby = !threatScanner.findNearbyThreats(
                creature, map.locationOf(creature), 2).isEmpty();
        hideTurnCount = threatNearby ? 0 : hideTurnCount + 1;

        // Branch 1: Exactly one actor nearby -> transition to TerritorialState.
        if (actorCount == 1)
        {
            // if the health is low, transit to feeling state.
            if (creature.getStatistic(ActorStatistics.HEALTH) < 50)
            {
                return BaseStates.getFleeingState();
            }
        }

        // Branch 2: More than one actor nearby -> stays c  amouflaged due to high threat.
        else if (actorCount > 1)
        {
            return BaseStates.getCamouflagedState();
        }

        // Branch 3: territory until no enemies nearby 10 turns -> transit to foraging state.
        if (this.hideTurnCount >= 10)
        {
            return BaseStates.getForagingState();
        }
        // else remains
        return this;
    }

    /**
     * Triggered exactly when the creature enters this state.
     *
     * @param creature the creature using this state
     * @param map the map containing the creature
     */
    @Override
    public void onEnter(SporeBeast creature, GameMap map) {
        // Ensure the turn counter is completely clean every time it enters the territorial state.
        this.hideTurnCount = 0;

    }
}
