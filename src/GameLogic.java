import java.util.*;

/**
 * The 'GameLogic' class represents the main logic for playing a board game, it implements the 'PlayableLogic' interface
 and provides the rules and methods needed to manage game play, including the game board, players and moves.
 * The game alternates between the two players, verifying moves, flipping discs and tracking the state of the game.
 * It supports cancel functionality and checks to end the game when there are no valid moves left and update win to the winner.
 *
 * BOARD_SIZE ---> Sets the size of the board (8x8).
 * Board ---> a two-dimensional array representing the game board, where each element is a `disc' object.
 * player1 and player2 ---> represent the two players in the game.
 * firstPlayerTurn ---> Player's turn.
 * MovesHistory ---> List of the last Move chronology.
 * LastMoves ---> A stack of List that saved the position that flipped.
 */
public class GameLogic implements PlayableLogic {
    private final int BOARD_SIZE = 8 ;
    private Disc[][] Board;
    private Player player1;
    private Player player2;
    private boolean firstPlayerTurn = true;
    private final List<Move> MovesHistory;
    private final Stack<HashSet<Position>> LastMoves;

    /** constructor:
     * Initializes a new instance of the GameLogic class.
     * - Sets up the game board as a 2D array of Disc objects with the specified size.
     * - Initializes an ArrayList to store the history of all moves played in the game.
     * - Initializes a Stack to keep track of the most recent moves, allowing for undo functionality.
     */
    public GameLogic() {
        this.Board = new Disc[BOARD_SIZE][BOARD_SIZE];
        MovesHistory = new ArrayList<>();
        LastMoves = new Stack<>();
    }


    /**
     * Sets the two players for the game.
     * @param player1 ---> player1 the first player to be set.
     * @param player2 ---> player2 the second player to be set.
     */
    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Gets the first player.
     * @return ---> the first player (player1).
     */
    @Override
    public Player getFirstPlayer() {return player1;}

    /**
     * Gets the second player.
     * @return ---> the second player (player2).
     */
    @Override
    public Player getSecondPlayer() {return player2;}

    /**
     * Checks if it's the first player's turn.
     * @return ---> true if it's the first player's turn, otherwise false.
     */
    @Override
    public boolean isFirstPlayerTurn() {return firstPlayerTurn;}

    /**
     * Returns the disc at the specified position on the board.
     * @param pos ---> The position for which to retrieve the disc.
     * @return ---> The disc at the given position, or null if there is no disc.
     * @throws IllegalArgumentException if the specified position is out of bounds.
     */
    @Override
    public Disc getDiscAtPosition(Position pos) {
        // Return the disc at the position (or null if the position is empty)
        return Board[pos.row()][pos.col()];
    }

    /**
     * Returns the size of the game board.
     * @return ---> the number of rows and columns in the board.
     */
    @Override
    public int getBoardSize() {return BOARD_SIZE;}

    /**
     * Resets the game board to its initial state, preparing it for a new game.
     * This method performs the following actions:
     -Initializes the game board (`Board`) as an empty 2D array with the correct size.
     -Clears the List of moves (`MoveHistory`) & ('LastMoves') to remove any previous game history.
     -reset_bombs_and_unflappable to thr players.
     -Places the initial discs on the board:
     * Two discs for Player 1 at positions ---> (3, 3) and (4, 4).
     * Two discs for Player 2 at positions ---> (3, 4) and (4, 3).
     * initialize 'firstPlayerTurn' to true.
     */
    @Override
    public void reset() {
        Board = new Disc[BOARD_SIZE][BOARD_SIZE];
        //initialize start Board
        Board[3][3]= new SimpleDisc(player1);
        Board[4][4]= new SimpleDisc(player1);

        Board[3][4]= new SimpleDisc(player2);
        Board[4][3]= new SimpleDisc(player2);

        player1.reset_bombs_and_unflippedable();
        player2.reset_bombs_and_unflippedable();
        MovesHistory.clear();
        LastMoves.clear();
        firstPlayerTurn = true;
    }

    /**
     * Checks the current game status to determine if the game has ended.
     * The game ends when the current player don't have any legal moves to make.
     * If the game is finished,counting the discs of players and winner is updated based on whose had the most discs.
     * Otherwise, the turn is switched to the other player.
     *
     * @return ---> true if the game is over (and update wins), false otherwise.
     */
    @Override
    public boolean isGameFinished() {

        int player1disc = 0;
        int player2disc = 0;

        if(ValidMoves().isEmpty()){
            //counting disc on board for each player.
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (Board[i][j] == null) {continue;}
                    if(Board[i][j].getOwner().equals(player1)){player1disc++;}
                    if(Board[i][j].getOwner().equals(player2)){player2disc++;}
                }
            }
            // Printing the Winner and the disc counting
            if (player1disc == player2disc) {
                System.out.println("\na tie both players had " + player1disc);
            }
            else if (player1disc > player2disc) {
                System.out.println("\nPlayer 1 wins with "+ player1disc + " disc! " + "Player 2 had " + player2disc + " disc");
                player1.addWin();
            }
            else {
                System.out.println("\nPlayer 2 wins with "+ player2disc + " disc! " + "Player 1 had " + player1disc + " disc");
                player2.addWin();
            }

            return true;
        }

        return false;
    }
    /**
     * Calculates and returns a list of all valid moves for the current player.
     * A move is valid if placing a disc in a certain position results in flipping at least one of the opponent's discs.
     * This method iterates over the entire board and identifies valid moves
     * based on the position of opponent discs and the rules of flipping.
     * @return ---> A list of valid positions where the current player can place a disc.
     */
    @Override
    public List<Position> ValidMoves() {
        ArrayList<Position> validMoves = new ArrayList<>();

        //check all the board if its valid position
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Position pos = new Position(i, j);
                if(isValidMove(pos)){
                    validMoves.add(pos);
                }
            }
        }
        return validMoves;
    }

    /**
     * Validates whether the given Position is allowed.
     * A Position is valid if:
     1. The target position is in the Board lines.
     2. The target position is empty (without disc)
     3. Placing a Disc in that position flips at least 1 opponent disc.
     * @param pos --->   The target position to validate.
     * @return true if the move is valid, false otherwise.
     */
    private boolean isValidMove(Position pos) {
        if (!inBounds(pos) || (Board[pos.row()][pos.col()] != null) ) {return false;}
        // Check if placing a disc here will flip any opponent discs
        return countFlips(pos) > 0;
    }

    /**
     * Checks if the given position is within the valid bounds of the game board (8x8).
     * @param position --->position the position object containing the row and column to check.
     * @return ---> true if the position is within bounds (0 to 7 for both row and column), otherwise false.
     */
    private boolean inBounds(Position position) {
        return (position.row() >= 0 && position.row() < BOARD_SIZE
                && position.col() >= 0 && position.col() < BOARD_SIZE);
    }

    /**
     * Places a disc on the board and updates the game state accordingly.
     *
     * This method:
     * 1. Validates if the move is legal according to the game rules.
     * 2. Ensures the player has enough special discs (e.g., BombDisc, UnflippableDisc) if used.
     * 3. Calculates the opponent's discs to flip based on the placed disc's position.
     * 4. Updates the board by placing the disc and flipping the necessary opponent discs.
     * 5. Tracks the move and flipped positions for undo functionality.
     * 6. Toggles the turn to the next player and prints the updated game status.
     *
     * @param a The position where the disc is to be placed.
     * @param disc The disc to be placed (e.g., SimpleDisc, BombDisc, UnflippableDisc).
     * @return true if the move is valid and executed successfully; false otherwise.
     */

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        // Validate the move
        if (!isValidMove(a)) {
            return false;
        }

        Player currentPlayer = isFirstPlayerTurn() ? getFirstPlayer() : getSecondPlayer();

        // Validate and reduce special disc counts
        if (!validateAndReduceSpecialDisc(currentPlayer, disc)) {
            return false;
        }

        Move thisMove = new Move(a, disc);
        HashSet<Position> movesToFlip = new HashSet<>();

        // Identify opponent discs to flip
        countflipdisc(a, currentPlayer, movesToFlip);

        // Place the disc on the board
        Board[a.row()][a.col()] = disc;

        // Flip the identified discs
        flipdisc(movesToFlip, currentPlayer);

        // Update history game state for Undo
        MovesHistory.add(thisMove);
        LastMoves.push(movesToFlip);

        // Print status
        StatusPrint(currentPlayer, true);

        // Switch turn
        firstPlayerTurn = !firstPlayerTurn;
        return true;
    }

    /**
     * Validates the availability of special discs (e.g., BombDisc, UnflippableDisc) for the current player
     * and reduces the count of the respective disc type if used.
     *
     * This method:
     * 1. Checks if the player has enough special discs of the specified type.
     * 2. Reduces the player's inventory of the disc type if the disc is valid and available.
     * 3. Prints an error message if the player attempts to use an unavailable special disc.
     *
     * @param player The current player attempting to place the disc.
     * @param disc The disc being placed (e.g., SimpleDisc, BombDisc, UnflippableDisc).
     * @return true if the disc is valid and available; false otherwise.
     */

    // Helper method to validate and reduce special disc counts
    private boolean validateAndReduceSpecialDisc(Player player, Disc disc) {
        String discType = disc.getType();

        if (discType.equals("⭕")) {
            if (player.getNumber_of_unflippedable() == 0) {
                return false;
            }
            player.reduce_unflippedable();
        } else if (discType.equals("💣")) {
            if (player.getNumber_of_bombs() == 0) {
                return false;
            }
            player.reduce_bomb();
        }
        return true;
    }

    /**
     * Generates all 8 possible directions in a 2D grid for navigation.
     * Directions include horizontal, vertical, and diagonal movements.
     */
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0},  // Vertical
            {0, -1}, {0, 1},  // Horizontal
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Diagonals
    };

    /**
     * Count the total number of opponent discs that would have been turned over if the current player placed a disc in the given position.
     *The method uses the following helper function "countflipdisc" to determine the amount disc that will flip in any direction.
     * @param a ---> The position where the current player is considering placing his disc.
     * @return  ---> The total number of disks to flip if the move is valid.
     */
    @Override
    public int countFlips(Position a) {
        Player currentPlayer = isFirstPlayerTurn() ? getFirstPlayer() : getSecondPlayer();
        HashSet<Position> flips = new HashSet<>();
        //count the flip disc for this position
        countflipdisc(a,currentPlayer,flips);
        return flips.size();
    }

    /**
     * Identifies all opponent discs that can be flipped as a result of placing a disc at the given position.
     *
     * This method:
     * 1. Iterates through all possible directions on the board (e.g., horizontal, vertical, diagonal).
     * 2. For each direction, determines if a valid sequence of opponent discs exists to flip.
     * 3. Adds the positions of the opponent discs to the provided set of positions (`moves`) if they can be flipped.
     *
     * @param move The position where the disc is placed.
     * @param player The current player making the move.
     * @param moves A set to store all positions of opponent discs that will be flipped.
     */
    private void countflipdisc(Position move, Player player, HashSet<Position> moves) {
        //count for all the direction and return "moves" Set.
        for (int[] direction : DIRECTIONS) {
            countflipindirectoin(move,direction[0],direction[1],player,moves);
        }
    }

    /**
     * Identifies and collects the positions of opponent discs that can be flipped in a specific direction.
     *
     * This method:
     * 1. Traverses the board from the given position (`pos`) in the specified direction (`dRow`, `dCol`).
     * 2. Collects opponent disc positions into a temporary set (`flipsInDirection`) until:
     *    - A disc belonging to the current player is encountered (valid flipping sequence).
     *    - An empty space or out-of-bounds position is encountered (invalid flipping sequence).
     * 3. Adds the collected positions to the main set (`moves`) if a valid flipping sequence is found.
     * 4. Handles special disc types:
     *    - Triggers the `trigerbomb` method for bomb discs (`💣`) to flip additional discs.
     *    - Ignores unflippable discs (`⭕`).
     *
     * @param pos The starting position for the directional search.
     * @param dRow The row increment for the direction to search.
     * @param dCol The column increment for the direction to search.
     * @param player The current player making the move.
     * @param moves A set to store all positions of opponent discs that will be flipped.
     */

    private void countflipindirectoin(Position pos, int dRow, int dCol, Player player, HashSet<Position> moves) {
        HashSet<Position> flipsInDirection = new HashSet<>();
        int currentRow = pos.row() + dRow;
        int currentCol = pos.col() + dCol;

        while (inBounds(new Position(currentRow, currentCol))) {
            Position currentPos = new Position(currentRow, currentCol);
            Disc currentDisc = Board[currentRow][currentCol];
            //if in that direction there no player disc so its not valid direction,
            // and we don't add all the disc that collected
            if (currentDisc == null || !inBounds(currentPos)) {
                break;
            }
            // if we reached player disc we add all that direction disc
            if (currentDisc.getOwner().equals(player)) {
                moves.addAll(flipsInDirection); // Valid flipping sequence
                return;
            }
            // make sure it's not "⭕"
            if(!currentDisc.getType().equals("⭕")) {
                // handle "💣" by send the to a recursive function
                if (currentDisc.getType().equals("💣")){
                    trigerbomb(currentPos,flipsInDirection,player);
                }
                flipsInDirection.add(currentPos);
            }
            // keep in the same direction
            currentRow += dRow;
            currentCol += dCol;
        }
    }

    /**
     * Recursively identifies all discs affected by a bomb disc placed on the board.
     *
     * This method:
     * 1. Adds the current bomb position to the set of affected positions (`bombmoves`).
     * 2. Iterates in all possible directions (e.g., horizontal, vertical, diagonal) from the bomb position.
     * 3. Checks neighboring positions for valid discs to affect:
     *    - Skips empty positions or positions with unflippable discs (`⭕`).
     *    - Adds positions occupied by opponent discs to the set of affected positions.
     * 4. Recursively triggers additional bomb effects if another bomb disc (`💣`) is encountered.
     * 5. Ensures each affected position is added only once to avoid redundant processing.
     *
     * @param bomb The position of the bomb disc triggering the effect.
     * @param bombmoves A set to store all positions affected by the bomb's chain reaction.
     * @param player The current player placing the bomb disc.
     */
    private void trigerbomb(Position bomb, HashSet<Position> bombmoves, Player player) {
        //adding the bomb position to avoid endless loop if its two neighbors bombs
        bombmoves.add(bomb);
        for (int[] direction : DIRECTIONS) {

            Position pos = new Position(bomb.row() + direction[0], bomb.col() + direction[1]);

            if (inBounds(pos) && !bombmoves.contains(pos) ){
                Disc nearbyDisc = getDiscAtPosition(pos);
                //if it's an empty space or "⭕" we continue
                if (nearbyDisc == null || nearbyDisc.getType().equals("⭕")) {continue;}
                // handle neighbors bombs recursive
                if (!nearbyDisc.getOwner().equals(player) ) {
                    if (nearbyDisc.getType().equals("💣")) {
                        trigerbomb(pos, bombmoves, player);
                    }
                    bombmoves.add(pos);
                }
            }
        }

    }

    /**
     * Updates the board by flipping the discs at the given positions.
     * Tracks the positions and discs flipped for potential undo functionality.
     * @param  currentPlayer   --->  the current player that the disc need to update.
     * @param  moves --->  The list of positions to flip.
     */
    private void flipdisc(HashSet<Position> moves, Player currentPlayer) {
        for (Position flip : moves) {
            getDiscAtPosition(flip).setOwner(currentPlayer);
        }

    }


    /**
     * Reverts the game state by undoing the last move made on the board.
     *
     * This method:
     * 1. Checks if there are any moves to undo. If not, it prints a message and returns.
     * 2. Retrieves and removes the last move from the move history (`MovesHistory`).
     * 3. Removes the disc placed during the last move from the board.
     * 4. Restores the flipped discs from the last move by reverting their ownership.
     * 5. Updates the player's inventory if the undone move involved a special disc:
     *    - Increases the unflippable disc count (`⭕`) for the opponent if it was used.
     *    - Increases the bomb disc count (`💣`) for the opponent if it was used.
     * 6. Prints the disc that flipped back reflecting the undone move.
     * 7. Switches the turn back to the previous player.
     *
     */

    @Override
        public void undoLastMove() {
        if (MovesHistory.isEmpty()) {
            System.out.println("\tNo previous move available to undo");
            return;
        }
        Player currentPlayer = isFirstPlayerTurn() ? getFirstPlayer() : getSecondPlayer();
        Player opposetPlayer = isFirstPlayerTurn() ? getSecondPlayer(): getFirstPlayer();

        //print all the undo actions
        StatusPrint(opposetPlayer,false);
        //remove the disc that placed
        Move lastMove = MovesHistory.removeLast();
        Position removedisc = lastMove.position();
        Board[removedisc.row()][removedisc.col()] = null;
        //flips back the disc from "LastMoves" List
        flipdisc(LastMoves.pop(),currentPlayer);
        //if the last disc was "⭕" , "💣" we add to the player
        if (lastMove.disc().getType().equals("⭕") ){
            opposetPlayer.increase_unflippedable();
        }
        if (lastMove.disc().getType().equals("💣")){
             opposetPlayer.increase_bomb();
        }
        //switching back to the players
        firstPlayerTurn = !firstPlayerTurn;

    }

    /**
     * Prints the game status to the console, reflecting the latest move or undo action.
     *
     * This method:
     * 1. Determines the player number (1 or 2) based on the `isPlayerOne` flag of the current player.
     * 2. Differentiates between placing a disc (`place_OR_undo` = true) and undoing a move (`place_OR_undo` = false).
     * 3. For a placed disc:
     *    - Prints the player number, the type of disc placed, and its position.
     *    - Iterates over the positions of flipped discs, printing their type and location.
     * 4. For an undone move:
     *    - Prints the player number, the type of disc removed, and its position.
     *    - Iterates over the positions of flipped discs, printing their type and location as they are reverted.
     *
     * @param ThePlayer The player making or undoing the move.
     * @param place_OR_undo A flag indicating whether the action is placing a disc (`true`) or undoing a move (`false`).
     */


    private void StatusPrint(Player ThePlayer, boolean place_OR_undo) {

        int player = 2;
        if(ThePlayer.isPlayerOne){player = 1;}
        // if its placed move
        if ((place_OR_undo)) {
            // print the disc that placed
            System.out.println("\nPlayer " +player
                    + " placed a " + MovesHistory.getLast().disc().getType()
                    + " in " + MovesHistory.getLast().position());
            // print the disc that flip
            for (Position move : LastMoves.peek()) {
                Disc disc = getDiscAtPosition(move);
                System.out.println("Player " + player
                        + " flipped the " + disc.getType()
                        + " in " + move);
            }
        }
        // if its undo
        else {
            // print the disc that removed
            System.out.println("\nPlayer " + player
                    + " removing " + MovesHistory.getLast().disc().getType()
                    + " from " + MovesHistory.getLast().position());
            // print the disc that flip back
            for (Position move : LastMoves.peek()) {
                Disc disc = getDiscAtPosition(move);
                System.out.println("Player " + player
                        + " flipped back " + disc.getType()
                        + " in " + move);
            }
        }
    }
 }

