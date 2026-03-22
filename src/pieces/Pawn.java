package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

public class Pawn extends Piece {
    public Pawn(String color, Position position) {
        super(color, position, color.equals("white") ? 'P' : 'p');
    }

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
                if (board.getPiece(oneStepRow, leftDiagCol) != null &&
                    !board.getPiece(oneStepRow, leftDiagCol).getColor().equals(this.color)) {
                    moves.add(new Position(oneStepRow, leftDiagCol));
                }
            }

            if (rightDiagCol < 8) {
                if (board.getPiece(oneStepRow, rightDiagCol) != null &&
                    !board.getPiece(oneStepRow, rightDiagCol).getColor().equals(this.color)) {
                    moves.add(new Position(oneStepRow, rightDiagCol));
                }
            }
        }

        return moves;
    }
}
