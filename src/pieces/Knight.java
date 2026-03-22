package pieces;

import java.util.*;
import utils.Position;
import board.Board;

public class Knight extends Piece {
    public Knight(String color, Position position) {
        super(color, position, color.equals("white") ? 'N' : 'n');
    }

    public List<Position> getPossibleMoves(Board board) {
        return new ArrayList<>();
    }
}
