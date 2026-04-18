package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

/**
 * Represents a king.
 */
public class King extends Piece {

    /**
     * Creates a king.
     *
     * @param color king color
     * @param position starting position
     */
    public King(String color, Position position) {
        super(color, position, color.equals("white") ? "wK" : "bK");
    }

    /**
     * Finds all possible moves for the king.
     *
     * @param board current board
     * @return list of king moves
     */
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();

        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) {
                    continue;
                }

                int r = row + dRow;
                int c = col + dCol;

                if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                    if (board.getPiece(r, c) == null || !board.getPiece(r, c).getColor().equals(this.color)) {
                        moves.add(new Position(r, c));
                    }
                }
            }
        }

        // Castling: king has not moved and is on its starting square
        boolean kingMoved = color.equals("white") ? board.isWhiteKingMoved() : board.isBlackKingMoved();
        if (!kingMoved && col == 4) {
            int backRank = color.equals("white") ? 7 : 0;
            if (row == backRank) {

                // King-side (O-O): squares f and g must be empty, rook on h must not have moved
                boolean rookHMoved = color.equals("white") ? board.isWhiteRookH1Moved() : board.isBlackRookH8Moved();
                if (!rookHMoved
                        && board.getPiece(backRank, 5) == null
                        && board.getPiece(backRank, 6) == null) {
                    moves.add(new Position(backRank, 6));
                }

                // Queen-side (O-O-O): squares b, c, d must be empty, rook on a must not have moved
                boolean rookAMoved = color.equals("white") ? board.isWhiteRookA1Moved() : board.isBlackRookA8Moved();
                if (!rookAMoved
                        && board.getPiece(backRank, 1) == null
                        && board.getPiece(backRank, 2) == null
                        && board.getPiece(backRank, 3) == null) {
                    moves.add(new Position(backRank, 2));
                }
            }
        }

        return moves;
    }
}
