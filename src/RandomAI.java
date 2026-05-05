import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an AI player that makes random moves in the game.
 * The `RandomAI` class extends the `AIPlayer` class and provides a basic implementation
 * for an AI player that selects moves randomly.
 */
public class RandomAI extends AIPlayer {

    /**
     * Constructs a new `RandomAI` player, associated with a specific player position (Player 1 or Player 2).
     *
     * @param isPlayerOne a boolean indicating whether the AI player is Player 1 (true) or Player 2 (false).
     */
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a random move for the AI player.
     *
     * @param gameStatus the current game state
     * @return the move selected by the RandomAI, or null if no valid moves exist
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Retrieve valid positions
        List<Position> validPositions = gameStatus.ValidMoves();
        if (validPositions.isEmpty()) {
            return null; // No valid moves available
        }

        // Determine the current player
        Player currentPlayer = gameStatus.isFirstPlayerTurn() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();

        // Filter available discs based on player's inventory
        List<Disc> availableDiscs = new ArrayList<>();
        if (currentPlayer.getNumber_of_bombs() > 0) {
            availableDiscs.add(new BombDisc(currentPlayer));
        }
        if (currentPlayer.getNumber_of_unflippedable() > 0) {
            availableDiscs.add(new UnflippableDisc(currentPlayer));
        }
        availableDiscs.add(new SimpleDisc(currentPlayer)); // SimpleDisc is always available

        // Select a random position and disc
        Random random = new Random();
        Position randomPosition = validPositions.get(random.nextInt(validPositions.size()));
        Disc randomDisc = availableDiscs.get(random.nextInt(availableDiscs.size()));

        // Return the random move
        return new Move(randomPosition, randomDisc);
    }
}
