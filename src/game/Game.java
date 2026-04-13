package game;

import board.Board;
import gui.ChessGUI;

/**
 * Starts the GUI version of the chess game.
 */
public class Game {

    /** Game board. */
    private Board board;

    /**
     * Creates a new game.
     */
    public Game() {
        board = new Board();
        board.initializeBoard();
    }

    /**
     * Starts the GUI game.
     */
    public void start() {
        javax.swing.SwingUtilities.invokeLater(() -> new ChessGUI(board));
    }
}
