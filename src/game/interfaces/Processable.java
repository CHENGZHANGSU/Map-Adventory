package game.interfaces;

import edu.monash.fit2099.engine.items.Item;

/**
 * This interface stores the methods the processable objects can perform.
 * It is a sub-interface inherited from Consumable.
 *
 * @author Su Chengzhang
 */
public interface Processable extends Consumable
{
    Item getItem();

    /** Applies this product and its layers, without changing inventory ownership. */
    String applyConsumptionEffects(edu.monash.fit2099.engine.actors.Actor actor,
                                   edu.monash.fit2099.engine.positions.GameMap map);
}
