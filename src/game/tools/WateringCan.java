package game.tools;

import edu.monash.fit2099.engine.items.Item;

/**
 * This class contain all the properties of a watering can.
 * It is sub-class inherited from Item class in engine package.
 *
 * @author Su Chengzhang
 */
public class WateringCan extends Item
{
    /**
     * This is a parameterized constructor used to initialize the WateringCan object.
     * @param name  The name of the WateringCan object.
     * @param displayChar   The character displayed represent the WateringCan object.
     */
    public WateringCan(String name, char displayChar)
    {
        super(name, displayChar);
        enableAbility(ToolAbility.WATER);
    }
}
