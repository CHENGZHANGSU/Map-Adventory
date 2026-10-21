package game.states;

import game.creatures.ForagingState;

/**
 * This class containing all the four states of the SporeBeast
 */
public class BaseStates {

    // Register the CamouflagedState
    static final CamouflagedState camouflagedState = new CamouflagedState();
    static final ForagingState foragingState = new ForagingState();
    static final TerritorialState territorialState = new TerritorialState();
    static final FleeingState fleeingState = new FleeingState();

    public static CamouflagedState getCamouflagedState() {
        return camouflagedState;
    }

    public static ForagingState getForagingState() {
        return foragingState;
    }

    public static TerritorialState getTerritorialState() {
        return territorialState;
    }

    public static FleeingState getFleeingState() {
        return fleeingState;
    }
}
