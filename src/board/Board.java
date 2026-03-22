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
        for (int col = 0; col < 8; col++) {
            grid[1][col] = new Pawn("black", new Position(1, col));
            grid[6][col] = new Pawn("white", new Position(6, col));
        }
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
}
