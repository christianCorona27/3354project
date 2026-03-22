package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

/**
 * Represents a pawn.
 */
public class Pawn extends Piece {

    /**
     * Creates a pawn.
     *
     * @param color pawn color
     * @param position starting position
     */
   public Pawn(String color, Position position) {
    super(color, position, color.equals("white") ? "wp" : "bp");
}
    /**
     * Finds all possible moves for the pawn.
     *
     * @param board current board
     * @return list of pawn moves
     */
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getCol();

        int direction;
        int startRow;

        if (color.equals("white")) {
            direction = -1;
            startRow = 6;
        } else {
            direction = 1;
            startRow = 1;
        }

        int oneStepRow = row + direction;
        if (oneStepRow >= 0 && oneStepRow < 8 && board.getPiece(oneStepRow, col) == null) {
            moves.add(new Position(oneStepRow, col));

            int twoStepRow = row + 2 * direction;
            if (row == startRow && twoStepRow >= 0 && twoStepRow < 8 && board.getPiece(twoStepRow, col) == null) {
                moves.add(new Position(twoStepRow, col));
            }
        }

        int leftDiagCol = col - 1;
        int rightDiagCol = col + 1;

        if (oneStepRow >= 0 && oneStepRow < 8) {
            if (leftDiagCol >= 0) {
                Piece leftPiece = board.getPiece(oneStepRow, leftDiagCol);
                if (leftPiece != null && !leftPiece.getColor().equals(color)) {
                    moves.add(new Position(oneStepRow, leftDiagCol));
                }
            }

            if (rightDiagCol < 8) {
                Piece rightPiece = board.getPiece(oneStepRow, rightDiagCol);
                if (rightPiece != null && !rightPiece.getColor().equals(color)) {
                    moves.add(new Position(oneStepRow, rightDiagCol));
                }
            }
        }

        return moves;
    }
}
