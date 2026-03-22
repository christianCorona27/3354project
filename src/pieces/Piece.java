package pieces;

import java.util.List;
import utils.Position;
import board.Board;

/**
 * Parent class for every chess piece.
 * Each piece has a color, position, and board symbol.
 */
public abstract class Piece {

    /** Color of the piece. */
    protected String color;

    /** Current position of the piece. */
    protected Position position;

    /** Character used to show the piece on the board. */
    protected char symbol;

    /**
     * Creates a piece with a color, position, and symbol.
     *
     * @param color piece color
     * @param position starting position
     * @param symbol board symbol
     */
    public Piece(String color, Position position, char symbol) {
        this.color = color;
        this.position = position;
        this.symbol = symbol;
    }

    /**
     * Gets the piece color.
     *
     * @return piece color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the current position.
     *
     * @return current position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Updates the current position.
     *
     * @param position new position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Gets the character used to display the piece.
     *
     * @return piece symbol
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Returns all legal moves for this piece.
     *
     * @param board current board
     * @return list of possible moves
     */
    public abstract List<Position> getPossibleMoves(Board board);
}
