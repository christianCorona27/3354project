package board;

import pieces.*;
import utils.Position;

public class Board {
	private Pieces[][] grid;

	public Board(){
		grid = new Piece[8][8];
	}

 	public Piece getPiece(int r, int c){
	return grid[r][c];
	}


	public void SetPiece(int r, int c, Piece p){
		grid[r][c] = p;
	}

	public void initializeBoard(){
		for (int c = 0; c<8; c++){
		grid [1][c] = new Pawn ("black", new Position(1,c));
		grid [6][c] = new Pawn ("white", new Position(6,c));
		}
	}
}
public void displayBoard(){
	System.out,println(" A B C D E F G H");
	for(int r = 0; r < 8; r++ ){
		System.out.print( 8 - r + " ");
		for (int c = 0; c < 8; c++){
		if (grid [r][c] == null) System.out.print(". ");
		else System.out.print(grid[r][c].getSymbol() + " ");

		}
		System.out.println();
	}
}
