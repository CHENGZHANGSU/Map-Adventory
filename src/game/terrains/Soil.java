package game.terrains;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * This class contain all the properties of a Soil object.
 * It is a sub-class inherited from Ground class in engine package.
 *
 * @author Su Chengzhang
 */
public class Soil extends Ground
{
    /**
     * This is a non-parameterized constructor used to instantiate a Soil object.
     */
    public Soil()
    {
        super(',', "Soil");
    }
}
