package game.tools;

import edu.monash.fit2099.engine.items.Item;
import game.interfaces.Breaker;

/**
 * A mining tool used to break rocks, nodes, and geodes.
 *
 * @author Zizhuo Wang
 */
public class Pickaxe extends Item implements Breaker {
    public Pickaxe() {
        super("Pickaxe", '^');
        this.makePortable();
        enableAbility(ToolAbility.DISCARD);
    }
}