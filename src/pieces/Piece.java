package pieces;

import java.util.List;
import utils.Position;
import board.Board;

public abstract class Piece {
	protected String color;
	protected Position position
	protected char symbol;

	public Piece(String color, Position position, char symbol){
		this.color = color;
		this.position = position;
		this.symbol = symbol;
	}

public String getColor() { return color; }
public Position getPosition() { return position; }
public void setPosition(Position p) { position = p; }
public char getSymbol() { return symbol; }

 public abstract List<Position> getPossibleMoves(Board board);
}
