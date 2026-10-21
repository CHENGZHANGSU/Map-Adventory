package game.tools;

import edu.monash.fit2099.engine.actors.Actor;

/** Queries abilities of tools actually carried in an actor's inventory. */
public final class ToolAccess {
    private ToolAccess() {
    }

    /**
     * Checks carried items rather than abilities attached directly to the actor.
     * @param actor actor whose inventory is inspected
     * @param ability ability the carried tool must provide
     * @return whether at least one carried item provides the ability
     */
    public static boolean hasCarriedTool(Actor actor, ToolAbility ability) {
        return actor.getInventory().getItems().stream()
                .anyMatch(item -> item.hasAbility(ability));
    }
}
