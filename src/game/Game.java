package game;

import java.util.Scanner;
import board.Board;
import utils.Position;

public class Game {
    private Board board;
    private String currentTurn;

    public Game() {
        board = new Board();
        currentTurn = "white";
    }

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

            System.out.println("Parsed move from (" + move[0].getRow() + "," + move[0].getCol() +
                    ") to (" + move[1].getRow() + "," + move[1].getCol() + ")");
        }

        scanner.close();
    }

    private Position[] parseMove(String input) {
        String[] parts = input.split(" ");
        if (parts.length != 2) {
            return null;
        }

        Position from = parseSquare(parts[0]);
        Position to = parseSquare(parts[1]);

        if (from == null || to == null) {
            return null;
        }

        return new Position[]{from, to};
    }

    private Position parseSquare(String square) {
        if (square.length() != 2) {
            return null;
        }

        char file = Character.toUpperCase(square.charAt(0));
        char rank = square.charAt(1);

        if (file < 'A' || file > 'H' || rank < '1' || rank > '8') {
            return null;
        }

        int col = file - 'A';
        int row = 8 - (rank - '0');

        return new Position(row, col);
    }
}
