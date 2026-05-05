/**
 *  Represents a position on a game board using a row and column.
 */

public class Position {
    /**
     * These are the department fields:
     *  row ---> The row index of the position.
     *  The ---> column index of the position.
     */
    private final int row;
    private final int col;

    /**
     * Constructs a Position object with the specified row and column.
     * @param row --->  row the row index of the position.
     * @param col ---> column the column index of the position.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Constructs a Position object with the specified position.
     * @param pos --->   the position.
     */

    public Position(Position pos) {
        this.row = pos.row;
        this.col = pos.col;
    }


    /**
     * Returns the row index of this position.
     * @return --->the row index.
     */
    public int row() {
        return this.row;
    }
    /**
     * Returns the column index of this position.
     * @return ---> the column index .
     */
    public int col() {
        return this.col;
    }

    /**
     * Compares this position with another object for equality.
     * Two positions are equal if their row and column indices are the same.
     * @param obj ---> the object to compare with
     * @return ---> true if the specified object is equal to this position, false otherwise
     */

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position pos = (Position) obj;
            return this.row == pos.row && this.col == pos.col;
        }

        return false;
    }

    /**
     * Returns a string representation of this position in the format (row,column).
     * @return ---> a string representation of the position
     */

    @Override
    public String toString() {
        return "(" + this.row + "," + this.col + ")";
    }

    @Override
    public int hashCode() {
        return 31 * this.row + this.col;
    }
}
