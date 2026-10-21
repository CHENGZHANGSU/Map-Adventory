package game.products.minerals;

import edu.monash.fit2099.engine.items.Item;

/**
 * A portable copper ore resource dropped by broken copper nodes.
 *
 * @author Zizhuo Wang
 */
public class CopperOre extends Item {

    /**
     * Creates portable copper ore.
     */
    public CopperOre() {
        super("Copper Ore", '+');
        makePortable();
    }
}
