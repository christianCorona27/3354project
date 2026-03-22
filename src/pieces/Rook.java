package pieces;

import java.util.*;
import utils.Position;
import board.Board;

public class Rook extends Piece {
    public Rook(String color, Position position) {
        super(color, position, color.equals("white") ? 'R' : 'r');
    }

    public List<Position> getPossibleMoves(Board board) {
        return new ArrayList<>();
    }
}
