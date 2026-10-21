
package game.products.crops;

import edu.monash.fit2099.engine.actors.Actor;
import game.interfaces.Processable;

public class PreservedProduce extends ProcessedProduce
{
    /**
     * Wraps the complete previous product in one preserved layer.
     *
     * @param precedingProduct the non-null product to decorate without changing it
     */
    public PreservedProduce(Processable precedingProduct) {
        super("Preserved ", precedingProduct);
    }

    @Override
    protected String applyProcessingEffect(Actor actor) {
        actor.heal(1);
        return actor + " restores 1 HP from the Preserved layer.";
    }
}