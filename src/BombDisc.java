/**
 * The BombDisc class represents a special type of disc in the game.
 * BombDisc is associated with the player and has a unique behavior
 *This class can be used to introduce game mechanics like explosion
 */


public class BombDisc extends Disc{
    /**
     * Constructs a BombDisc with the specified owner.
     * @param player ---> the owner of the BombDisc
     */
    public BombDisc(Player player) {
        super(player);
    }
    /**
     * Returns the type of this disc as a string representation.
     * In this case, it is represented as a bomb emoji ("💣").
     * @return a string representing the type of the disc
     */
    @Override
    public String getType() {
        return "💣";
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
