package game;

import board.Board;

public class Game {
	private Board board;


	public Game(){
	board = new Board();
	}

	public void start (){
		board.initializeBoard();
		board.displayBoard();
	}

}
