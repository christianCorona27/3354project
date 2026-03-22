package pieces;

import java.util.List;
import utils.Position;
import board.Board;

/**
 * Abstract base class for all chess pieces.
 */
public abstract class Piece {
    protected String color;
    protected Position position;
    protected String symbol;

    /**
     * Creates a piece with a color, board position, and display symbol.
     *
     * @param color the piece color
     * @param position the piece position
     * @param symbol the text symbol used for board display
     */
    public Piece(String color, Position position, String symbol) {
        this.color = color;
        this.position = position;
        this.symbol = symbol;
    }

    public String getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getSymbol() {
        return symbol;
    }

    public abstract List<Position> getPossibleMoves(Board board);
}
