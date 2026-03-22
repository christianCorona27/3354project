package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

/**
 * Represents a knight.
 */
public class Knight extends Piece {

    /**
     * Creates a knight.
     *
     * @param color knight color
     * @param position starting position
     */
    public Knight(String color, Position position) {
        super(color, position, color.equals("white") ? "wN" : "bn");
    }

    /**
     * Finds all possible moves for the knight.
     *
     * @param board current board
     * @return list of knight moves
     */
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        int[][] offsets = {
            {-2, -1}, {-2, 1},
            {-1, -2}, {-1, 2},
            {1, -2}, {1, 2},
            {2, -1}, {2, 1}
        };

        for (int[] offset : offsets) {
            int r = row + offset[0];
            int c = col + offset[1];

            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                if (board.getPiece(r, c) == null || !board.getPiece(r, c).getColor().equals(this.color)) {
                    moves.add(new Position(r, c));
                }
            }
        }

        return moves;
    }
}
