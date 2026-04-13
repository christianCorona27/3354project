package gui;

import javax.swing.JButton;

/**
 * Represents one square on the chessboard GUI.
 */
public class SquareButton extends JButton {

    private int row;
    private int col;

    /**
     * Creates a square button with a row and column.
     *
     * @param row board row
     * @param col board column
     */
    public SquareButton(int row, int col) {
        this.row = row;
        this.col = col;
        setFocusPainted(false);
    }

    /**
     * Gets the board row.
     *
     * @return row
     */
    public int getBoardRow() {
        return row;
    }

    /**
     * Gets the board column.
     *
     * @return column
     */
    public int getBoardCol() {
        return col;
    }
}
