package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

/**
 * Represents a rook.
 */
public class Rook extends Piece {

    /**
     * Creates a rook.
     *
     * @param color rook color
     * @param position starting position
     */
    public Rook(String color, Position position) {
        super(color, position, color.equals("white") ? "wR" : "bR");
    }

    /**
     * Finds all possible moves for the rook.
     *
     * @param board current board
     * @return list of rook moves
     */
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        addDirection(board, moves, row, col, -1, 0);
        addDirection(board, moves, row, col, 1, 0);
        addDirection(board, moves, row, col, 0, -1);
        addDirection(board, moves, row, col, 0, 1);

        return moves;
    }

    /**
     * Adds moves in one straight direction until blocked.
     *
     * @param board current board
     * @param moves move list
     * @param row current row
     * @param col current column
     * @param dRow row change
     * @param dCol column change
     */
    private void addDirection(Board board, List<Position> moves, int row, int col, int dRow, int dCol) {
        int r = row + dRow;
        int c = col + dCol;

        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
            if (board.getPiece(r, c) == null) {
                moves.add(new Position(r, c));
            } else {
                if (!board.getPiece(r, c).getColor().equals(this.color)) {
                    moves.add(new Position(r, c));
                }
                break;
            }
            r += dRow;
            c += dCol;
        }
    }
}
