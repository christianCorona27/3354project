package board;

import pieces.*;
import utils.Position;

/**
 * Represents the chess board and the pieces on it.
 * Supports full move execution, en passant tracking, and castling rights.
 */
public class Board {

    /** 8x8 board of chess pieces. */
    private Piece[][] grid;

    /**
     * Tracks the column of a pawn that just moved two squares (for en passant).
     * -1 means no en passant target on this turn.
     */
    private int enPassantCol = -1;

    /**
     * Tracks the row where an en passant capture would land.
     * -1 means none.
     */
    private int enPassantRow = -1;

    // Castling rights flags
    private boolean whiteKingMoved  = false;
    private boolean blackKingMoved  = false;
    private boolean whiteRookA1Moved = false; // queen-side
    private boolean whiteRookH1Moved = false; // king-side
    private boolean blackRookA8Moved = false;
    private boolean blackRookH8Moved = false;

    /**
     * Creates an empty board.
     */
    public Board() {
        grid = new Piece[8][8];
    }

    // ── Getters / setters ────────────────────────────────────────────────

    public Piece getPiece(int row, int col) { return grid[row][col]; }
    public void  setPiece(int row, int col, Piece piece) { grid[row][col] = piece; }

    public int  getEnPassantCol() { return enPassantCol; }
    public int  getEnPassantRow() { return enPassantRow; }
    public void setEnPassant(int row, int col) { enPassantRow = row; enPassantCol = col; }
    public void clearEnPassant() { enPassantRow = -1; enPassantCol = -1; }

    public boolean isWhiteKingMoved()   { return whiteKingMoved; }
    public boolean isBlackKingMoved()   { return blackKingMoved; }
    public boolean isWhiteRookA1Moved() { return whiteRookA1Moved; }
    public boolean isWhiteRookH1Moved() { return whiteRookH1Moved; }
    public boolean isBlackRookA8Moved() { return blackRookA8Moved; }
    public boolean isBlackRookH8Moved() { return blackRookH8Moved; }

    public void setWhiteKingMoved(boolean v)   { whiteKingMoved = v; }
    public void setBlackKingMoved(boolean v)   { blackKingMoved = v; }
    public void setWhiteRookA1Moved(boolean v) { whiteRookA1Moved = v; }
    public void setWhiteRookH1Moved(boolean v) { whiteRookH1Moved = v; }
    public void setBlackRookA8Moved(boolean v) { blackRookA8Moved = v; }
    public void setBlackRookH8Moved(boolean v) { blackRookH8Moved = v; }

    // ── Board setup ──────────────────────────────────────────────────────

    /**
     * Sets up the pieces in their starting positions.
     */
    public void initializeBoard() {
        grid = new Piece[8][8];
        clearEnPassant();
        whiteKingMoved = blackKingMoved = false;
        whiteRookA1Moved = whiteRookH1Moved = false;
        blackRookA8Moved = blackRookH8Moved = false;

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
     * Moves a piece from one position to another, tracking castling rights
     * and en passant state.
     *
     * @param from starting position
     * @param to   ending position
     */
    public void movePiece(Position from, Position to) {
        Piece piece = getPiece(from.getRow(), from.getCol());
        clearEnPassant();

        if (piece instanceof Pawn) {
            int rowDiff = Math.abs(to.getRow() - from.getRow());
            if (rowDiff == 2) {
                // Two-square pawn push: record en passant square
                int epRow = (from.getRow() + to.getRow()) / 2;
                setEnPassant(epRow, to.getCol());
            }
        }

        // Track castling rights
        if (piece instanceof King) {
            if (piece.getColor().equals("white")) whiteKingMoved = true;
            else                                  blackKingMoved = true;
        }
        if (piece instanceof Rook) {
            if (from.getRow() == 7 && from.getCol() == 0) whiteRookA1Moved = true;
            if (from.getRow() == 7 && from.getCol() == 7) whiteRookH1Moved = true;
            if (from.getRow() == 0 && from.getCol() == 0) blackRookA8Moved = true;
            if (from.getRow() == 0 && from.getCol() == 7) blackRookH8Moved = true;
        }

        setPiece(to.getRow(), to.getCol(), piece);
        setPiece(from.getRow(), from.getCol(), null);

        if (piece != null) {
            piece.setPosition(new Position(to.getRow(), to.getCol()));
        }
    }

    /**
     * Creates a deep copy of the board for undo and move-validation purposes.
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

        copy.enPassantRow   = this.enPassantRow;
        copy.enPassantCol   = this.enPassantCol;
        copy.whiteKingMoved  = this.whiteKingMoved;
        copy.blackKingMoved  = this.blackKingMoved;
        copy.whiteRookA1Moved = this.whiteRookA1Moved;
        copy.whiteRookH1Moved = this.whiteRookH1Moved;
        copy.blackRookA8Moved = this.blackRookA8Moved;
        copy.blackRookH8Moved = this.blackRookH8Moved;

        return copy;
    }

    /**
     * Makes a copy of a piece.
     */
    private Piece clonePiece(Piece piece, int row, int col) {
        Position newPos = new Position(row, col);
        if (piece instanceof Pawn)   return new Pawn(piece.getColor(), newPos);
        if (piece instanceof Rook)   return new Rook(piece.getColor(), newPos);
        if (piece instanceof Knight) return new Knight(piece.getColor(), newPos);
        if (piece instanceof Bishop) return new Bishop(piece.getColor(), newPos);
        if (piece instanceof Queen)  return new Queen(piece.getColor(), newPos);
        if (piece instanceof King)   return new King(piece.getColor(), newPos);
        return null;
    }
}
