package pieces;

import java.util.*;
import utils.Position;
import board.Board;

public class Pawn extends Piece {
	public Pawn(String color, Position position){
	super(color, position, color.equals("white") ? 'P' : 'p');
	}

	public List<Position> getPossibleMoves(Board board){
		return new ArrayList<>();
		}

}
