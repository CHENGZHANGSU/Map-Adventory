package game.interfaces;

/**
 * Contract for ground types capable of holding an electrical charge.
 *
 * @author Zizhuo Wang
 */
public interface Chargeable {
    /**
     * Sets the charged state of the entity.
     *
     * @param charged true if charged, false otherwise.
     */
    void setCharged(boolean charged);

    /**
     * Checks if the entity is currently holding an electrical charge.
     *
     * @return true if it is charged, false otherwise.
     */
    boolean isCharged();
}