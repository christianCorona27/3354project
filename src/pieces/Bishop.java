package pieces;

import java.util.*;
import utils.Position;
import board.Board;

public class Bishop extends Piece {
    public Bishop(String color, Position position) {
        super(color, position, color.equals("white") ? 'B' : 'b');
    }

    public List<Position> getPossibleMoves(Board board) {
        return new ArrayList<>();
    }
}
