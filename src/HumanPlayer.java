/**
 * The HumanPlayer class represents a human player in the game, extending the Player class.
 * This class overrides the `isHuman` method to return true, indicating that the player is human.
        */
public class HumanPlayer extends Player{


    /**
     * Constructor to create a new HumanPlayer instance.
     * @param isPlayerOne ---> a boolean indicating whether this player is player one (true) or player two (false)
     */
    public HumanPlayer(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Indicates that this player is human.
     * @return ---> true, as this player is a human
     */
    @Override
    boolean isHuman() {
        return true;
    }
}
