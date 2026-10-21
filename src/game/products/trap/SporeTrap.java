package game.products.trap;

import edu.monash.fit2099.engine.items.Item;
import game.actors.Player;

/**
 * This class stores all the entities and behaviors of the SporeTrap object.
 * It is a sub-class inherit from Trap super class.
 *
 * @author Su Chengzhang
 */
public class SporeTrap extends Trap
{
    public SporeTrap()
    {
        super("Spore Trap", '#');
        this.makeNonPortable();
    }

    @Override
    public String extraEffect(Player player)
    {
        if (player != null)
        {
            // dropped all items effects when player stand on the trap.
            int n = player.getInventory().getItems().size();
            for (int i = 0; i < n; i++)
            {
                Item item = player.getInventory().getItems().getFirst();
                player.getInventory().remove(item);
            }
            return String.format("%s is caught by the trap, lost all items😢.", player);
        }
        return null;
    }

}
