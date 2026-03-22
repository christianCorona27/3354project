package pieces;

import java.util.ArrayList;
import java.util.List;
import utils.Position;
import board.Board;

public class King extends Piece {
    public King(String color, Position position) {
        super(color, position, color.equals("white") ? 'K' : 'k');
    }

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

        return moves;
    }
}
