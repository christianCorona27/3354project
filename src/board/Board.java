package board;

import pieces.*;
import utils.Position;

import java.util.List;

/**
 * Represents the chess board and the pieces on it.
 */
public class Board {

    /** 8x8 board of chess pieces. */
    private Piece[][] grid;

    /**
     * Creates an empty board.
     */
    public Board() {
        grid = new Piece[8][8];
    }

    /**
     * Gets the piece at a row and column.
     *
     * @param row board row
     * @param col board column
     * @return piece at that spot or null
     */
    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    /**
     * Places a piece at a row and column.
     *
     * @param row board row
     * @param col board column
     * @param piece piece to place
     */
    public void setPiece(int row, int col, Piece piece) {
        grid[row][col] = piece;
    }

    /**
     * Sets up the pieces in their starting positions.
     */
    public void initializeBoard() {
        grid = new Piece[8][8];

        for (int col = 0; col < 8; col++) {
            grid[1][col] = new Pawn("black", new Position(1, col));
            grid[6][col] = new Pawn("white", new Position(6, col));
        }

        grid[0][0] = new Rook("black", new Position(0, 0));
        grid[0][7] = new Rook("black", new Position(0, 7));
        grid[7][0] = new Rook("white", new Position(7, 0));
        grid[7][7] = new Rook("white", new Position(7, 7));

        grid[0][1] = new Knight("black", new Position(0, 1));
        grid[0][6] = new Knight("black", new Position(0, 6));
        grid[7][1] = new Knight("white", new Position(7, 1));
        grid[7][6] = new Knight("white", new Position(7, 6));

        grid[0][2] = new Bishop("black", new Position(0, 2));
        grid[0][5] = new Bishop("black", new Position(0, 5));
        grid[7][2] = new Bishop("white", new Position(7, 2));
        grid[7][5] = new Bishop("white", new Position(7, 5));

        grid[0][3] = new Queen("black", new Position(0, 3));
        grid[7][3] = new Queen("white", new Position(7, 3));

        grid[0][4] = new King("black", new Position(0, 4));
        grid[7][4] = new King("white", new Position(7, 4));
    }

    /**
     * Prints the board to the console.
     */
    public void displayBoard() {
        System.out.println("   A   B   C   D   E   F   G   H");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " ");
            for (int col = 0; col < 8; col++) {
                if (grid[row][col] == null) {
                    System.out.print(" ## ");
                } else {
                    System.out.print(" " + grid[row][col].getSymbol() + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Moves a piece from one position to another.
     *
     * @param from starting position
     * @param to ending position
     */
    public void movePiece(Position from, Position to) {
        Piece piece = getPiece(from.getRow(), from.getCol());
        setPiece(to.getRow(), to.getCol(), piece);
        setPiece(from.getRow(), from.getCol(), null);

        if (piece != null) {
            piece.setPosition(new Position(to.getRow(), to.getCol()));
        }
    }

    /**
     * Returns true if a move follows piece movement rules and does not leave
     * the moving side's king in check.
     *
     * @param from source square
     * @param to destination square
     * @param movingColor side to move
     * @return true if legal under current simplified rule-set
     */
    public boolean isLegalMove(Position from, Position to, String movingColor) {
        Piece moving = getPiece(from.getRow(), from.getCol());
        if (moving == null || !moving.getColor().equals(movingColor)) {
            return false;
        }

        Piece target = getPiece(to.getRow(), to.getCol());
        if (target != null && target.getColor().equals(movingColor)) {
            return false;
        }

        // Kings cannot be captured; checkmate must end the game.
        if (target instanceof King) {
            return false;
        }

        boolean canReach = false;
        List<Position> pseudoLegal = moving.getPossibleMoves(this);
        for (Position pos : pseudoLegal) {
            if (pos.getRow() == to.getRow() && pos.getCol() == to.getCol()) {
                canReach = true;
                break;
            }
        }

        if (!canReach) {
            return false;
        }

        Board simulated = copyBoard();
        simulated.movePiece(from, to);
        return !simulated.isInCheck(movingColor);
    }

    /**
     * Finds whether the given side's king is currently under attack.
     *
     * @param color side to evaluate
     * @return true if that king is in check
     */
    public boolean isInCheck(String color) {
        Position kingPosition = findKing(color);
        if (kingPosition == null) {
            return false;
        }

        String opponent = color.equals("white") ? "black" : "white";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece == null || !piece.getColor().equals(opponent)) {
                    continue;
                }

                List<Position> moves = piece.getPossibleMoves(this);
                for (Position move : moves) {
                    if (move.getRow() == kingPosition.getRow() && move.getCol() == kingPosition.getCol()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Finds whether the given side has at least one legal move.
     *
     * @param color side to evaluate
     * @return true if at least one legal move exists
     */
    public boolean hasAnyLegalMove(String color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece == null || !piece.getColor().equals(color)) {
                    continue;
                }

                List<Position> moves = piece.getPossibleMoves(this);
                Position from = new Position(row, col);
                for (Position to : moves) {
                    if (isLegalMove(from, to, color)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the provided side is checkmated.
     *
     * @param color side to evaluate
     * @return true if in check and has no legal move
     */
    public boolean isCheckmate(String color) {
        return isInCheck(color) && !hasAnyLegalMove(color);
    }

    /**
     * Determines if the provided side is stalemated.
     *
     * @param color side to evaluate
     * @return true if not in check and has no legal move
     */
    public boolean isStalemate(String color) {
        return !isInCheck(color) && !hasAnyLegalMove(color);
    }

    /**
     * Locates a side's king.
     *
     * @param color side to search
     * @return board position of the king, or null if absent
     */
    private Position findKing(String color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPiece(row, col);
                if (piece instanceof King && piece.getColor().equals(color)) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Creates a copy of the board for undo support.
     *
     * @return copied board
     */
    public Board copyBoard() {
        Board copy = new Board();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = grid[row][col];
                if (piece != null) {
                    copy.setPiece(row, col, clonePiece(piece, row, col));
                }
            }
        }

        return copy;
    }

    /**
     * Makes a copy of a piece.
     *
     * @param piece piece to clone
     * @param row new row
     * @param col new col
     * @return copied piece
     */
    private Piece clonePiece(Piece piece, int row, int col) {
        Position newPos = new Position(row, col);

        if (piece instanceof Pawn) {
            return new Pawn(piece.getColor(), newPos);
        } else if (piece instanceof Rook) {
            return new Rook(piece.getColor(), newPos);
        } else if (piece instanceof Knight) {
            return new Knight(piece.getColor(), newPos);
        } else if (piece instanceof Bishop) {
            return new Bishop(piece.getColor(), newPos);
        } else if (piece instanceof Queen) {
            return new Queen(piece.getColor(), newPos);
        } else if (piece instanceof King) {
            return new King(piece.getColor(), newPos);
        }

        return null;
    }
}
