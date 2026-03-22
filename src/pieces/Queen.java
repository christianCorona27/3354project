package pieces;

import java.util.*;
import utils.Position;
import board.Board;

public class Queen extends Piece {
    public Queen(String color, Position position) {
        super(color, position, color.equals("white") ? 'Q' : 'q');
    }

    public List<Position> getPossibleMoves(Board board) {
        return new ArrayList<>();
    }
}
