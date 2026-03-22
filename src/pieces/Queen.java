package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

/**
 * Represents a queen.
 */
public class Queen extends Piece {

    /**
     * Creates a queen.
     *
     * @param color queen color
     * @param position starting position
     */
    public Queen(String color, Position position) {
        super(color, position, color.equals("white") ? 'wQ' : 'bQ');
    }

    /**
     * Finds all possible moves for the queen.
     *
     * @param board current board
     * @return list of queen moves
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
        addDirection(board, moves, row, col, -1, -1);
        addDirection(board, moves, row, col, -1, 1);
        addDirection(board, moves, row, col, 1, -1);
        addDirection(board, moves, row, col, 1, 1);

        return moves;
    }

    /**
     * Adds moves in one direction until blocked.
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
