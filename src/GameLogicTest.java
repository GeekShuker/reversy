import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    private GameLogic game;
    private HumanPlayer p1;
    private HumanPlayer p2;

    @BeforeEach
    void setUp() {
        game = new GameLogic();
        p1 = new HumanPlayer(true);
        p2 = new HumanPlayer(false);
        game.setPlayers(p1, p2);
        game.reset();
        // Post-reset board: p1 at (3,3),(4,4)  |  p2 at (3,4),(4,3)
    }

    // -----------------------------------------------------------------------
    // Position — equals / hashCode
    // -----------------------------------------------------------------------

    @Test
    void position_equalCoordinatesAreEqual() {
        assertEquals(new Position(3, 4), new Position(3, 4));
    }

    @Test
    void position_differentCoordinatesAreNotEqual() {
        assertNotEquals(new Position(3, 4), new Position(4, 3));
    }

    @Test
    void position_hashCodeConsistentForSameCoords() {
        assertEquals(new Position(5, 2).hashCode(), new Position(5, 2).hashCode());
    }

    @Test
    void position_hashCodeDistinctForSymmetricCoords() {
        // Before the fix, row+col gave identical hashes for (0,1) and (1,0).
        assertNotEquals(new Position(0, 1).hashCode(), new Position(1, 0).hashCode());
    }

    @Test
    void position_hashCodeDistinctForAnotherSymmetricPair() {
        assertNotEquals(new Position(2, 5).hashCode(), new Position(5, 2).hashCode());
    }

    // -----------------------------------------------------------------------
    // Initial board state
    // -----------------------------------------------------------------------

    @Test
    void initialBoard_hasFourDiscs() {
        int count = 0;
        for (int r = 0; r < game.getBoardSize(); r++)
            for (int c = 0; c < game.getBoardSize(); c++)
                if (game.getDiscAtPosition(new Position(r, c)) != null) count++;
        assertEquals(4, count);
    }

    @Test
    void initialBoard_player1DiscsAtCorrectPositions() {
        assertEquals(p1, game.getDiscAtPosition(new Position(3, 3)).getOwner());
        assertEquals(p1, game.getDiscAtPosition(new Position(4, 4)).getOwner());
    }

    @Test
    void initialBoard_player2DiscsAtCorrectPositions() {
        assertEquals(p2, game.getDiscAtPosition(new Position(3, 4)).getOwner());
        assertEquals(p2, game.getDiscAtPosition(new Position(4, 3)).getOwner());
    }

    @Test
    void initialBoard_cornersAreEmpty() {
        assertNull(game.getDiscAtPosition(new Position(0, 0)));
        assertNull(game.getDiscAtPosition(new Position(0, 7)));
        assertNull(game.getDiscAtPosition(new Position(7, 0)));
        assertNull(game.getDiscAtPosition(new Position(7, 7)));
    }

    @Test
    void initialBoard_isNotFinished() {
        assertFalse(game.isGameFinished());
    }

    @Test
    void initialBoard_hasFourValidMoves() {
        assertEquals(4, game.ValidMoves().size());
    }

    // -----------------------------------------------------------------------
    // Valid / invalid moves
    // -----------------------------------------------------------------------

    @Test
    void validMoves_containsAllKnownOpeningMoves() {
        List<Position> moves = game.ValidMoves();
        assertTrue(moves.contains(new Position(2, 4)));
        assertTrue(moves.contains(new Position(3, 5)));
        assertTrue(moves.contains(new Position(4, 2)));
        assertTrue(moves.contains(new Position(5, 3)));
    }

    @Test
    void invalidMove_occupiedCell_notInValidMoves() {
        List<Position> moves = game.ValidMoves();
        assertFalse(moves.contains(new Position(3, 3))); // occupied by p1
        assertFalse(moves.contains(new Position(3, 4))); // occupied by p2
    }

    @Test
    void invalidMove_cornerWithNoFlip_notInValidMoves() {
        assertFalse(game.ValidMoves().contains(new Position(0, 0)));
    }

    // -----------------------------------------------------------------------
    // countFlips
    // -----------------------------------------------------------------------

    @Test
    void countFlips_allOpeningMovesFlipOneDisc() {
        assertEquals(1, game.countFlips(new Position(2, 4)));
        assertEquals(1, game.countFlips(new Position(3, 5)));
        assertEquals(1, game.countFlips(new Position(4, 2)));
        assertEquals(1, game.countFlips(new Position(5, 3)));
    }

    @Test
    void countFlips_occupiedCell_returnsZero() {
        // (3,3) is p1's disc; no valid flip sequence originates from it
        assertEquals(0, game.countFlips(new Position(3, 3)));
    }

    @Test
    void countFlips_emptyCorner_returnsZero() {
        assertEquals(0, game.countFlips(new Position(0, 0)));
    }

    // -----------------------------------------------------------------------
    // locate_disc
    // -----------------------------------------------------------------------

    @Test
    void locateDisc_validMove_returnsTrue() {
        assertTrue(game.locate_disc(new Position(2, 4), new SimpleDisc(p1)));
    }

    @Test
    void locateDisc_placesDiscAtTargetPosition() {
        game.locate_disc(new Position(2, 4), new SimpleDisc(p1));
        Disc placed = game.getDiscAtPosition(new Position(2, 4));
        assertNotNull(placed);
        assertEquals(p1, placed.getOwner());
    }

    @Test
    void locateDisc_flipsOpponentDiscInLine() {
        // (3,4) starts owned by p2; placing at (2,4) creates line (2,4)-(3,4)-(4,4)
        game.locate_disc(new Position(2, 4), new SimpleDisc(p1));
        assertEquals(p1, game.getDiscAtPosition(new Position(3, 4)).getOwner());
    }

    @Test
    void locateDisc_switchesTurnToOtherPlayer() {
        assertTrue(game.isFirstPlayerTurn());
        game.locate_disc(new Position(2, 4), new SimpleDisc(p1));
        assertFalse(game.isFirstPlayerTurn());
    }

    @Test
    void locateDisc_occupiedCell_returnsFalse() {
        assertFalse(game.locate_disc(new Position(3, 3), new SimpleDisc(p1)));
    }

    @Test
    void locateDisc_noFlipPossible_returnsFalse() {
        assertFalse(game.locate_disc(new Position(0, 0), new SimpleDisc(p1)));
    }

    @Test
    void locateDisc_doesNotChangeTurnOnFailure() {
        game.locate_disc(new Position(0, 0), new SimpleDisc(p1)); // invalid
        assertTrue(game.isFirstPlayerTurn()); // still p1's turn
    }

    // -----------------------------------------------------------------------
    // undoLastMove
    // -----------------------------------------------------------------------

    @Test
    void undoLastMove_removesPlacedDisc() {
        game.locate_disc(new Position(2, 4), new SimpleDisc(p1));
        game.undoLastMove();
        assertNull(game.getDiscAtPosition(new Position(2, 4)));
    }

    @Test
    void undoLastMove_restoresFlippedDisc() {
        // (3,4) was p2's; after move it's p1's; after undo it's p2's again
        game.locate_disc(new Position(2, 4), new SimpleDisc(p1));
        game.undoLastMove();
        assertEquals(p2, game.getDiscAtPosition(new Position(3, 4)).getOwner());
    }

    @Test
    void undoLastMove_restoresTurnToPreviousPlayer() {
        game.locate_disc(new Position(2, 4), new SimpleDisc(p1));
        assertFalse(game.isFirstPlayerTurn()); // p2's turn
        game.undoLastMove();
        assertTrue(game.isFirstPlayerTurn()); // back to p1
    }

    @Test
    void undoLastMove_onEmptyHistory_doesNotThrow() {
        assertDoesNotThrow(() -> game.undoLastMove());
    }

    @Test
    void undoLastMove_restoresUnflippableDiscCount() {
        assertEquals(2, p1.getNumber_of_unflippedable());
        game.locate_disc(new Position(2, 4), new UnflippableDisc(p1)); // uses 1
        assertEquals(1, p1.getNumber_of_unflippedable());
        game.undoLastMove();
        assertEquals(2, p1.getNumber_of_unflippedable());
    }

    @Test
    void undoLastMove_restoresBombCount() {
        assertEquals(3, p1.getNumber_of_bombs());
        game.locate_disc(new Position(2, 4), new BombDisc(p1)); // uses 1
        assertEquals(2, p1.getNumber_of_bombs());
        game.undoLastMove();
        assertEquals(3, p1.getNumber_of_bombs());
    }

    // -----------------------------------------------------------------------
    // isGameFinished
    // -----------------------------------------------------------------------

    @Test
    void isGameFinished_returnsFalseAtStart() {
        assertFalse(game.isGameFinished());
    }

    @Test
    void isGameFinished_returnsTrueWhenNoCellsEmpty() throws Exception {
        // Fill every cell — no empty squares means no valid moves for either player
        Field boardField = GameLogic.class.getDeclaredField("Board");
        boardField.setAccessible(true);
        Disc[][] board = (Disc[][]) boardField.get(game);
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = new SimpleDisc(p1);
        assertTrue(game.isGameFinished());
    }

    @Test
    void isGameFinished_fullBoard_incrementsWinnerWins() throws Exception {
        // Board full of p1 discs → p1 wins → p1.getWins() increments
        int winsBefore = p1.getWins();
        Field boardField = GameLogic.class.getDeclaredField("Board");
        boardField.setAccessible(true);
        Disc[][] board = (Disc[][]) boardField.get(game);
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = new SimpleDisc(p1);
        game.isGameFinished();
        assertEquals(winsBefore + 1, p1.getWins());
    }

    // -----------------------------------------------------------------------
    // BombDisc — chain explosion
    // -----------------------------------------------------------------------

    @Test
    void bombDisc_triggersChainFlipToNeighbors() throws Exception {
        // Board set up via reflection (all other cells null):
        //
        //   (2,3): p2 SimpleDisc  ← bomb neighbor — should cascade-flip to p1
        //   (3,2): p1 SimpleDisc  ← anchor that terminates the flip chain
        //   (3,3): p2 BombDisc    ← triggered when the chain reaches it
        //   (3,4): p2 SimpleDisc  ← first disc in chain going left from (3,5)
        //   (4,3): p2 SimpleDisc  ← bomb neighbor — should cascade-flip to p1
        //
        // Player1 places SimpleDisc at (3,5).
        // Chain going left: (3,4)[p2] → (3,3)[p2 Bomb, explodes] → (3,2)[p1 anchor].
        // Bomb explosion adds neighbours (2,3) and (4,3) to the flip set.
        Field boardField = GameLogic.class.getDeclaredField("Board");
        boardField.setAccessible(true);
        Disc[][] board = new Disc[8][8];
        board[2][3] = new SimpleDisc(p2);
        board[3][2] = new SimpleDisc(p1);
        board[3][3] = new BombDisc(p2);
        board[3][4] = new SimpleDisc(p2);
        board[4][3] = new SimpleDisc(p2);
        boardField.set(game, board);

        boolean placed = game.locate_disc(new Position(3, 5), new SimpleDisc(p1));
        assertTrue(placed, "Move at (3,5) should be valid with this board setup");

        // Discs directly in the flip chain:
        assertEquals(p1, game.getDiscAtPosition(new Position(3, 4)).getOwner(),
                "(3,4) should flip to p1 via normal chain");
        assertEquals(p1, game.getDiscAtPosition(new Position(3, 3)).getOwner(),
                "(3,3) bomb disc itself should flip to p1");

        // Discs flipped by the bomb's cascade explosion:
        assertEquals(p1, game.getDiscAtPosition(new Position(2, 3)).getOwner(),
                "(2,3) bomb neighbour should cascade-flip to p1");
        assertEquals(p1, game.getDiscAtPosition(new Position(4, 3)).getOwner(),
                "(4,3) bomb neighbour should cascade-flip to p1");

        // Anchor disc (p1's own) must not change owner:
        assertEquals(p1, game.getDiscAtPosition(new Position(3, 2)).getOwner(),
                "(3,2) p1 anchor must remain p1");
    }

    // -----------------------------------------------------------------------
    // GreedyAI
    // -----------------------------------------------------------------------

    @Test
    void greedyAI_returnsNonNullMoveFromOpeningPosition() {
        GreedyAI ai = new GreedyAI(true);
        assertNotNull(ai.makeMove(game));
    }

    @Test
    void greedyAI_picksMoveThatMatchesMaximumFlipCount() {
        GreedyAI ai = new GreedyAI(true);
        Move move = ai.makeMove(game);
        int chosenFlips = game.countFlips(move.position());
        int maxFlips = game.ValidMoves().stream()
                .mapToInt(game::countFlips)
                .max()
                .orElse(0);
        assertEquals(maxFlips, chosenFlips,
                "GreedyAI must pick a move with the maximum number of flips");
    }

    @Test
    void greedyAI_tiebreak_picksRightmostColumnLowestRow() {
        // All 4 opening moves flip 1 disc.
        // GreedyAI tiebreaks: rightmost column first → (3,5) wins (col 5 > 4 > 3 > 2).
        GreedyAI ai = new GreedyAI(true);
        Move move = ai.makeMove(game);
        assertEquals(new Position(3, 5), move.position(),
                "GreedyAI should pick (3,5) — highest column among equal-flip opening moves");
    }

    @Test
    void greedyAI_moveDiscIsOwnedByCurrentPlayer() {
        GreedyAI ai = new GreedyAI(true);
        Move move = ai.makeMove(game);
        assertEquals(p1, move.disc().getOwner());
    }
}
