package game.util;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/** Game-level statistic operations that do not refill current points. */
public final class Statistics {
    private Statistics() {
    }

    /**
     * The engine refills points on a maximum increase, so restore the previous value.
     * @param actor actor whose statistic changes
     * @param statistic statistic whose maximum increases
     * @param amount amount added to the maximum
     */
    public static void increaseMaximum(Actor actor, ActorStatistics statistic, int amount) {
        int current = actor.getStatistic(statistic);
        actor.modifyStatisticMaximum(statistic, StatisticOperations.INCREASE, amount);
        actor.modifyStatistic(statistic, StatisticOperations.UPDATE, current);
    }
}
