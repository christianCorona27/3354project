package pieces;

import java.util.*;
import utils.Position;
import board.Board;

public class King extends Piece {
    public King(String color, Position position) {
        super(color, position, color.equals("white") ? 'K' : 'k');
    }

    public List<Position> getPossibleMoves(Board board) {
        return new ArrayList<>();
    }
}
