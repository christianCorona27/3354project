package utils;

/**
 * Stores a spot on the chess board using row and column.
 */
public class Position {

    /** Row on the board. */
    private int row;

    /** Column on the board. */
    private int col;

    /**
     * Creates a position with a row and column.
     *
     * @param row row value
     * @param col column value
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row.
     *
     * @return row value
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column.
     *
     * @return column value
     */
    public int getCol() {
        return col;
    }

    /**
     * Changes the row.
     *
     * @param row new row value
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Changes the column.
     *
     * @param col new column value
     */
    public void setCol(int col) {
        this.col = col;
    }
}
