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
        List<Position> validPositions = gameStatus.ValidMoves();

        if (validPositions.isEmpty()) {
            return null;
        }

        // Compute flip count once per position to avoid redundant calls during sort/filter
        Map<Position, Integer> flipCounts = new HashMap<>();
        for (Position pos : validPositions) {
            flipCounts.put(pos, gameStatus.countFlips(pos));
        }

        // Sort by flip count descending
        validPositions.sort((a, b) -> Integer.compare(flipCounts.get(b), flipCounts.get(a)));

        // Collect all positions tied at the maximum flip count
        int maxFlips = flipCounts.get(validPositions.getFirst());
        List<Position> maxFlipPositions = new ArrayList<>();
        for (Position pos : validPositions) {
            if (flipCounts.get(pos) == maxFlips) {
                maxFlipPositions.add(pos);
            } else {
                break;
            }
        }

        // Tiebreak: rightmost column first, then highest row index first
        maxFlipPositions.sort((a, b) -> {
            if (a.col() == b.col()) {
                return Integer.compare(b.row(), a.row());
            }
            return Integer.compare(b.col(), a.col());
        });

        Player currentPlayer = gameStatus.isFirstPlayerTurn() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();
        return new Move(maxFlipPositions.getFirst(), new SimpleDisc(currentPlayer));
    }

    }






