/**
 * The `SimpleDisc` class is a subclass of the `Disc` class, which represents a basic game tool that can be rotated in Reversi board games.
 * Each `SimpleDisc` is associated with a specific player and is displayed on the game board using the type (`⬤`).
 * This class provides the basic behavior of a standard game tool, including identifying its type and belonging to the player.
 */
public class SimpleDisc extends Disc {
    /**
     * Constructs a new `SimpleDisc` associated with the specified player
     * @param player ---> player the player who owns this disc.
     */
    public SimpleDisc(Player player) {
        super(player);
    }

    /**
     * Returns the visual representation of the disc's type.
     * @return ---> a string representing the type of the disc, which is "⬤".
     */

    @Override
    public String getType() {
        return "⬤";
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


