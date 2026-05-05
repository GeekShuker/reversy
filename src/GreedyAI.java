import java.util.*;

/**
 * Represents an AI player making the "greedy" move in the game.
 * The `GreedyAI` class extends the `AIPlayer` class and provides a basic implementation
 * For an AI player who will ideally make moves to maximize their immediate profit in the game.
 */
public class GreedyAI extends AIPlayer{

    /**
     * Constructs a new `GreedyAI` player, associated with a specific player position (Player 1 or Player 2).
     * @param isPlayerOne ---> isPlayerOne a boolean indicating whether the AI player is Player 1 (true) or Player 2 (false).
     */
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a greedy move for the AI player, where the AI selects the move that maximizes its advantage.
     * @param gameStatus --->  the current game state (includes the game board, current player)
     * @return ---> ?
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Retrieve valid moves
        List<Position> validPositions = gameStatus.ValidMoves();

        if (validPositions.isEmpty()) {
            return null; // No valid moves available
        }

        // Sort valid positions by number of flips (descending order)
        validPositions.sort(new Comparator<Position>() {
            @Override
            public int compare(Position p1, Position p2) {
                return Integer.compare(gameStatus.countFlips(p2), gameStatus.countFlips(p1));
            }
        });


        // Filter positions with maximum flips
        int maxFlips = gameStatus.countFlips(validPositions.getFirst()); // Get the highest number of flips

        List<Position> maxFlipPositions = new ArrayList<>();
        for (Position pos : validPositions) {
            if (gameStatus.countFlips(pos) == maxFlips) {
                maxFlipPositions.add(pos);
            } else {
                break; // Stop after reaching positions with fewer flips
            }
        }


        // Sort filtered positions by location (rightmost column, lowest row)
        maxFlipPositions.sort(new Comparator<Position>() {
            @Override
            public int compare(Position p1, Position p2) {
                if (p1.col() == p2.col()) {
                    return Integer.compare(p2.row(), p1.row()); // Lowest row first
                }
                return Integer.compare(p2.col(), p1.col()); // Rightmost column first
            }
        });


        // Determine the current player and create a disc
        Player currentPlayer = gameStatus.isFirstPlayerTurn() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();
        Disc disc = new SimpleDisc(currentPlayer);

        // Return the best move
        return new Move(maxFlipPositions.getFirst(), disc);
    }

    }






