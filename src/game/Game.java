package game;

import java.util.List;
import java.util.Scanner;
import board.Board;
import pieces.Piece;
import utils.Position;

/**
 * Runs the chess game loop and handles player input.
 */
public class Game {

    /** Game board. */
    private Board board;

    /** Keeps track of whose turn it is. */
    private String currentTurn;

    /**
     * Creates a new game.
     */
    public Game() {
        board = new Board();
        currentTurn = "white";
    }

    /**
     * Starts the game and keeps it running until the user quits.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        board.initializeBoard();

        while (true) {
            board.displayBoard();
            System.out.println(currentTurn + "'s turn.");
            System.out.print("Enter move (example: E2 E4) or type quit: ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Game ended.");
                break;
            }

            Position[] move = parseMove(input);
            if (move == null) {
                System.out.println("Invalid input format. Use E2 E4.");
                continue;
            }

            Position from = move[0];
            Position to = move[1];

            Piece piece = board.getPiece(from.getRow(), from.getCol());

            if (piece == null) {
                System.out.println("No piece at that starting square.");
                continue;
            }

            if (!piece.getColor().equals(currentTurn)) {
                System.out.println("That is not your piece.");
                continue;
            }

            List<Position> possibleMoves = piece.getPossibleMoves(board);
            boolean validMove = false;

            for (Position pos : possibleMoves) {
                if (pos.getRow() == to.getRow() && pos.getCol() == to.getCol()) {
                    validMove = true;
                    break;
                }
            }

            if (!validMove) {
                System.out.println("Invalid move for that piece.");
                continue;
            }

            board.movePiece(from, to);
            switchTurn();
        }

        scanner.close();
    }

    /**
     * Changes the turn from white to black or black to white.
     */
    private void switchTurn() {
        currentTurn = currentTurn.equals("white") ? "black" : "white";
    }

    /**
     * Reads a move like E2 E4 and turns it into board positions.
     *
     * @param input move entered by the player
     * @return array with start and end positions, or null if invalid
     */
    private Position[] parseMove(String input) {
        String[] parts = input.split(" ");
        if (parts.length != 2) {
            return null;
        }

        Position from = parsePosition(parts[0]);
        Position to = parsePosition(parts[1]);

        if (from == null || to == null) {
            return null;
        }

        return new Position[]{from, to};
    }

    /**
     * Converts a chess square like E2 into row and column values.
     *
     * @param text square entered by the player
     * @return matching position or null if invalid
     */
    private Position parsePosition(String text) {
        if (text.length() != 2) {
            return null;
        }

        char file = Character.toUpperCase(text.charAt(0));
        char rank = text.charAt(1);

        if (file < 'A' || file > 'H' || rank < '1' || rank > '8') {
            return null;
        }

        int col = file - 'A';
        int row = 8 - (rank - '0');

        return new Position(row, col);
    }
}
