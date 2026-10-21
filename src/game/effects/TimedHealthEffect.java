package game.effects;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/** An independent health effect, updated and expired by the engine once per turn. */
public final class TimedHealthEffect implements Status {
    private final int changePerTurn;
    private int remainingTurns;

    /**
     * Creates an effect; each consumed food attaches its own instance.
     * @param changePerTurn positive to heal, negative to damage
     * @param duration number of future turns to apply the change
     * @throws IllegalArgumentException if duration is not positive
     */
    public TimedHealthEffect(int changePerTurn, int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        this.changePerTurn = changePerTurn;
        this.remainingTurns = duration;
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (!isStatusActive()) {
            return;
        }
        // A healing effect must not revive an actor killed by another effect.
        if (entity.hasStatistic(ActorStatistics.HEALTH)
                && entity.getStatistic(ActorStatistics.HEALTH) > 0) {
            StatisticOperations operation = changePerTurn < 0
                    ? StatisticOperations.DECREASE : StatisticOperations.INCREASE;
            entity.modifyStatistic(ActorStatistics.HEALTH, operation, Math.abs(changePerTurn));
        }
        remainingTurns--;
    }

    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }
}
