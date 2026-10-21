package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.SporeBeast;
import game.interfaces.CreatureState;
import game.products.trap.SporeTrap;
import game.states.BaseStates;
import game.states.fleeing.EscapeRoutePlanner;
import game.states.fleeing.SafetyEvaluator;
import game.states.fleeing.ThreatScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REQ5 state that moves the SporeBeast away from nearby threats.
 */
public class FleeingState implements CreatureState {

    private static final int THREAT_SCAN_RADIUS = 2;
    private static final int ADJACENT_THREAT_RADIUS = 1;

    private final ThreatScanner threatScanner;
    private final EscapeRoutePlanner escapeRoutePlanner;

    /**
     * Creates a fleeing state with standard scanning and route components.
     */
    public FleeingState() {
        this(new ThreatScanner(), new EscapeRoutePlanner(new SafetyEvaluator()));
    }

    /**
     * Creates a fleeing state with supplied components.
     *
     * @param threatScanner component that detects threats
     * @param escapeRoutePlanner component that plans a safe route
     */
    public FleeingState(
            ThreatScanner threatScanner,
            EscapeRoutePlanner escapeRoutePlanner) {
        this.threatScanner = threatScanner;
        this.escapeRoutePlanner = escapeRoutePlanner;
    }

    /**
     * Moves one tile along the best safe route away from nearby threats.
     *
     * @param creature the fleeing creature
     * @param map map containing the creature
     * @return a move action, or a no-op action when no escape is possible
     */
    @Override
    public Action createTurnAction(SporeBeast creature, GameMap map) {
        if (!map.contains(creature) || !creature.isConscious()) {
            return new DoNothingAction();
        }

        Location currentLocation = map.locationOf(creature);
        List<Actor> threats = threatScanner.findNearbyThreats(
                creature,
                currentLocation,
                THREAT_SCAN_RADIUS);

        if (threats.isEmpty()) {
            return new DoNothingAction();
        }

        List<Location> threatLocations = findThreatLocations(map, threats);
        Optional<Exit> escapeExit = escapeRoutePlanner.findEscapeExit(
                creature,
                currentLocation,
                threatLocations);

        if (escapeExit.isEmpty()) {
            return new DoNothingAction();
        }

        Exit exit = escapeExit.get();
        return exit.getDestination().getMoveAction(
                creature,
                exit.getName(),
                exit.getHotKey());
    }

    /**
     * Chooses the next state using deterministic threat conditions.
     * The beast remains fleeing while it is outnumbered or a single threat
     * is still at range. Once a sole threat closes to an adjacent tile, the
     * beast becomes territorial; when no threats remain nearby, it returns
     * to camouflage.
     *
     * @param creature the fleeing creature
     * @param map map containing the creature
     * @return the next deterministic state
     */
    @Override
    public CreatureState getNextState(SporeBeast creature, GameMap map) {
        if (!map.contains(creature) || !creature.isConscious()) {
            return this;
        }

        Location currentLocation = map.locationOf(creature);
        List<Actor> threats = threatScanner.findNearbyThreats(
                creature,
                currentLocation,
                THREAT_SCAN_RADIUS);

        if (threats.isEmpty()) {
            return BaseStates.getCamouflagedState();
        }

        if (threats.size() == 1
                && threatScanner.findNearbyThreats(
                        creature,
                        currentLocation,
                        ADJACENT_THREAT_RADIUS).size() == 1) {
            return BaseStates.getTerritorialState();
        }

        return this;
    }

    /**
     * Deploys a one-time spore barrier when the beast enters fleeing.
     * Empty, enterable adjacent tiles are seeded with traps so that a pursuer
     * must consider hazardous routes while the beast starts its escape.
     * Existing traps are preserved to avoid duplicating hazards.
     *
     * @param creature the creature entering fleeing
     * @param map map containing the creature
     */
    @Override
    public void onEnter(SporeBeast creature, GameMap map) {
        if (!map.contains(creature)) {
            return;
        }

        Location currentLocation = map.locationOf(creature);

        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();

            if (!adjacentLocation.containsAnActor()
                    && adjacentLocation.getGround().canActorEnter(creature)
                    && !containsSporeTrap(adjacentLocation)) {
                adjacentLocation.addItem(new SporeTrap());
            }
        }
    }

    /**
     * Gets the current locations of each detected threat.
     *
     * @param map map containing the threats
     * @param threats detected threats
     * @return locations occupied by the threats
     */
    private List<Location> findThreatLocations(
            GameMap map,
            List<Actor> threats) {
        List<Location> locations = new ArrayList<>();

        for (Actor threat : threats) {
            locations.add(map.locationOf(threat));
        }

        return locations;
    }

    /**
     * Checks whether a location already contains a spore trap.
     *
     * @param location location to inspect
     * @return true if a spore trap is already present
     */
    private boolean containsSporeTrap(Location location) {
        return location.getItems()
                .stream()
                .anyMatch(item -> item instanceof SporeTrap);
    }
}
