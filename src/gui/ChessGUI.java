package gui;

import board.Board;
import pieces.King;
import pieces.Piece;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
 * GUI for the chess game.
 */
public class ChessGUI extends JFrame {

    private Board board;
    private SquareButton[][] buttons;
    private Position selectedPosition;
    private String currentTurn;

    private JLabel statusLabel;
    private JTextArea moveHistoryArea;

    private Stack<Board> boardHistory;
    private Stack<String> turnHistory;

    /**
     * Creates the chess GUI window.
     *
     * @param board game board
     */
    public ChessGUI(Board board) {
        this.board = board;
        this.buttons = new SquareButton[8][8];
        this.selectedPosition = null;
        this.currentTurn = "white";
        this.boardHistory = new Stack<>();
        this.turnHistory = new Stack<>();

        setTitle("Chess GUI - Phase 2");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createMenuBar();
        add(createBoardPanel(), BorderLayout.CENTER);
        add(createSidePanel(), BorderLayout.EAST);

        refreshBoard();
        setVisible(true);
    }

    /**
     * Creates the menu bar.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> resetGame());

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> undoMove());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.add(undoItem);
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Creates the 8x8 board panel.
     *
     * @return board panel
     */
    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                SquareButton button = new SquareButton(row, col);
                button.setFont(new Font("SansSerif", Font.BOLD, 26));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                setSquareColor(button, row, col);

                button.addActionListener(e -> handleSquareClick(button.getBoardRow(), button.getBoardCol()));

                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        return boardPanel;
    }

    /**
     * Creates the side panel with turn label, move history, and buttons.
     *
     * @return side panel
     */
    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(250, 700));

        statusLabel = new JLabel("Turn: White", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        sidePanel.add(statusLabel, BorderLayout.NORTH);

        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        sidePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        buttonPanel.add(undoButton);
        buttonPanel.add(resetButton);

        sidePanel.add(buttonPanel, BorderLayout.SOUTH);

        return sidePanel;
    }

    /**
     * Handles a click on a board square.
     *
     * @param row clicked row
     * @param col clicked column
     */
    private void handleSquareClick(int row, int col) {
        Piece clickedPiece = board.getPiece(row, col);

        if (selectedPosition == null) {
            if (clickedPiece == null) {
                return;
            }

            if (!clickedPiece.getColor().equals(currentTurn)) {
                JOptionPane.showMessageDialog(this, "It is " + currentTurn + "'s turn.");
                return;
            }

            selectedPosition = new Position(row, col);
            highlightSelectedSquare(row, col);
            return;
        }

        int fromRow = selectedPosition.getRow();
        int fromCol = selectedPosition.getCol();

        if (fromRow == row && fromCol == col) {
            selectedPosition = null;
            refreshBoard();
            return;
        }

        Piece movingPiece = board.getPiece(fromRow, fromCol);
        if (movingPiece == null) {
            selectedPosition = null;
            refreshBoard();
            return;
        }

        Piece destinationPiece = board.getPiece(row, col);

        if (destinationPiece != null && destinationPiece.getColor().equals(currentTurn)) {
            selectedPosition = new Position(row, col);
            refreshBoard();
            highlightSelectedSquare(row, col);
            return;
        }

        boardHistory.push(board.copyBoard());
        turnHistory.push(currentTurn);

        String moveText = currentTurn + ": " + toChessSquare(fromRow, fromCol)
                + " -> " + toChessSquare(row, col);

        if (destinationPiece != null) {
            moveText += " captured " + destinationPiece.getSymbol();
        }

        boolean capturedKing = destinationPiece instanceof King;

        board.movePiece(new Position(fromRow, fromCol), new Position(row, col));
        moveHistoryArea.append(moveText + "\n");

        selectedPosition = null;
        refreshBoard();

        if (capturedKing) {
            JOptionPane.showMessageDialog(this,
                    capitalize(currentTurn) + " wins by capturing the king!");
            System.exit(0);
        }

        switchTurn();
    }

    /**
     * Switches turns.
     */
    private void switchTurn() {
        currentTurn = currentTurn.equals("white") ? "black" : "white";
        statusLabel.setText("Turn: " + capitalize(currentTurn));
    }

    /**
     * Resets the game.
     */
    private void resetGame() {
        board = new Board();
        board.initializeBoard();
        selectedPosition = null;
        currentTurn = "white";
        boardHistory.clear();
        turnHistory.clear();
        moveHistoryArea.setText("");
        statusLabel.setText("Turn: White");
        refreshBoard();
    }

    /**
     * Undoes the last move.
     */
    private void undoMove() {
        if (boardHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No moves to undo.");
            return;
        }

        board = boardHistory.pop();
        currentTurn = turnHistory.pop();
        selectedPosition = null;
        refreshBoard();
        statusLabel.setText("Turn: " + capitalize(currentTurn));

        String[] lines = moveHistoryArea.getText().split("\n");
        if (lines.length > 0 && !moveHistoryArea.getText().isEmpty()) {
            StringBuilder rebuilt = new StringBuilder();
            for (int i = 0; i < lines.length - 1; i++) {
                if (!lines[i].trim().isEmpty()) {
                    rebuilt.append(lines[i]).append("\n");
                }
            }
            moveHistoryArea.setText(rebuilt.toString());
        }
    }

    /**
     * Refreshes all board squares.
     */
    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                SquareButton button = buttons[row][col];
                setSquareColor(button, row, col);

                Piece piece = board.getPiece(row, col);
                if (piece == null) {
                    button.setText("");
                } else {
                    button.setText(piece.getSymbol());
                }
            }
        }
    }

    /**
     * Highlights the selected square.
     *
     * @param row selected row
     * @param col selected column
     */
    private void highlightSelectedSquare(int row, int col) {
        refreshBoard();
        buttons[row][col].setBackground(Color.YELLOW);
    }

    /**
     * Sets light or dark board color.
     *
     * @param button square button
     * @param row row
     * @param col col
     */
    private void setSquareColor(SquareButton button, int row, int col) {
        if ((row + col) % 2 == 0) {
            button.setBackground(new Color(240, 217, 181));
        } else {
            button.setBackground(new Color(181, 136, 99));
        }
    }

    /**
     * Converts row and column into chess notation.
     *
     * @param row board row
     * @param col board col
     * @return square like E4
     */
    private String toChessSquare(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Capitalizes a word.
     *
     * @param text text
     * @return capitalized text
     */
    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
