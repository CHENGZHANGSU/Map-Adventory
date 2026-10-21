package game.tools;

import edu.monash.fit2099.engine.items.Item;

/**
 * This class contain all the properties of a Hoe.
 * It is sub-class inherited from Item class in engine package.
 *
 * @author Su Chengzhang
 */
public class Hoe extends Item
{
    /**
     * This is a parameterized constructor used to create a Hoe object.
     * @param displayChar   The character used to display as a Hoe.
     * @param name  The name of the Hoe object.
     */
    public Hoe(String name, char displayChar)
    {
        super(name, displayChar);
    }

}
