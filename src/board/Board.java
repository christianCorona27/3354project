package board;

import pieces.*;
import utils.Position;

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

