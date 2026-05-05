/**
 * Represents a special type of disc that cannot be flipped during the game.
 *The `UnflippableDisc` class extends the `Disc` class and is associated with a specific player.
 * Unlike a regular disc, this disc remains static and does not participate in the inversion mechanics.
 *  It is visually represented by the symbol "⭕".
 */
public class UnflippableDisc extends Disc{

    /**
     * Constructs an `UnflippableDisc` associated with the specified player.
     * @param player ---> player the player who owns this disc.
     */
    public UnflippableDisc(Player player) {
        super(player);
    }

    /**
     * Returns the visual representation of the disc's type.
     * @return ---> a string representing the type of the disc, which is "⭕".
     */
    @Override
    public String getType() {
        return "⭕";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Disc){
            Disc other = (Disc) obj;
            return this.getType().equals(other.getType());
        }
        return false;
    }

}
