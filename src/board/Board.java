package board;

import pieces.*;
import utils.Position;

public class Board {
    private Piece[][] grid;

    public Board() {
        grid = new Piece[8][8];
    }

    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        grid[row][col] = piece;
    }

    public void initializeBoard() {
        // Pawns
        for (int col = 0; col < 8; col++) {
            grid[1][col] = new Pawn("black", new Position(1, col));
            grid[6][col] = new Pawn("white", new Position(6, col));
        }

        // Rooks
        grid[0][0] = new Rook("black", new Position(0, 0));
        grid[0][7] = new Rook("black", new Position(0, 7));
        grid[7][0] = new Rook("white", new Position(7, 0));
        grid[7][7] = new Rook("white", new Position(7, 7));

        // Knights
        grid[0][1] = new Knight("black", new Position(0, 1));
        grid[0][6] = new Knight("black", new Position(0, 6));
        grid[7][1] = new Knight("white", new Position(7, 1));
        grid[7][6] = new Knight("white", new Position(7, 6));

        // Bishops
        grid[0][2] = new Bishop("black", new Position(0, 2));
        grid[0][5] = new Bishop("black", new Position(0, 5));
        grid[7][2] = new Bishop("white", new Position(7, 2));
        grid[7][5] = new Bishop("white", new Position(7, 5));

        // Queens
        grid[0][3] = new Queen("black", new Position(0, 3));
        grid[7][3] = new Queen("white", new Position(7, 3));

        // Kings
        grid[0][4] = new King("black", new Position(0, 4));
        grid[7][4] = new King("white", new Position(7, 4));
    }

    public void displayBoard() {
        System.out.println("  A B C D E F G H");
        for (int row = 0; row < 8; row++) {
            System.out.print(8 - row + " ");
            for (int col = 0; col < 8; col++) {
                if (grid[row][col] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(grid[row][col].getSymbol() + " ");
                }
            }
            System.out.println();
        }
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = grid[from.getRow()][from.getCol()];

        if (piece == null) {
            return false;
        }

        grid[to.getRow()][to.getCol()] = piece;
        grid[from.getRow()][from.getCol()] = null;

        piece.setPosition(to);
        return true;
    }
}
