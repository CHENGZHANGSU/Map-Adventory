package game.terrains.facilities;


import game.interfaces.Processable;
import game.products.crops.PreservedProduce;
import game.products.crops.ProcessedProduce;

/**
 * This is a class representing preserves jar on the ground.
 * It is a sub-class inherited from Ground from engine package, implementing Processable interface.
 *
 * @author Su Chengzhang
 */
public class PreservesJar extends Facility
{

    public PreservesJar()
    {
        super('J', "Preserved Jar");
    }

    @Override
    public ProcessedProduce process(Processable product)
    {
        return new PreservedProduce(product);
    }

}
