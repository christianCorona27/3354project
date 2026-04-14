package gui;

import board.Board;
import pieces.King;
import pieces.Piece;
import utils.Position;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Main GUI window for the chess game.
 * Handles board display, click-to-move, drag-and-drop, move history,
 * captured pieces, undo, save/load game, and settings customisation.
 */
public class ChessGUI extends JFrame {

    // ── Board state
    private Board board;
    private SquareButton[][] buttons;
    private Position selectedPosition;
    private String currentTurn;

    // ── Undo stacks
    private Stack<Board> boardHistory;
    private Stack<String> turnHistory;
    private Stack<String[]> capturedHistory;

    // ── Side-panel widgets
    private JLabel statusLabel;
    private JTextArea moveHistoryArea;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private JPanel sidePanel;

    // ── Settings
    private Color lightSquareColor = new Color(240, 217, 181);
    private Color darkSquareColor  = new Color(181, 136,  99);
    private int   squareSize       = 80;

    // ── Drag state
    private int dragFromRow = -1;
    private int dragFromCol = -1;

    // ── Captured piece lists
    private List<String> whiteCaptured = new ArrayList<>();
    private List<String> blackCaptured = new ArrayList<>();

    /**
     * Creates the chess GUI window and starts the game.
     *
     * @param board the initial game board
     */
    public ChessGUI(Board board) {
        this.board           = board;
        this.buttons         = new SquareButton[8][8];
        this.selectedPosition = null;
        this.currentTurn     = "white";
        this.boardHistory    = new Stack<>();
        this.turnHistory     = new Stack<>();
        this.capturedHistory = new Stack<>();

        setTitle("Chess Game – Phase 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        createMenuBar();
        add(createBoardPanel(), BorderLayout.CENTER);
        add(createSidePanel(), BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        refreshBoard();
        setVisible(true);
    }


    //  MENU BAR

    /**
     * Builds the menu bar with Game and Settings menus.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem newItem  = new JMenuItem("New Game");
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem loadItem = new JMenuItem("Load Game");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem exitItem = new JMenuItem("Exit");

        newItem.addActionListener(e  -> resetGame());
        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());
        undoItem.addActionListener(e -> undoMove());
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(undoItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem settingsItem = new JMenuItem("Board & Piece Style…");
        settingsItem.addActionListener(e -> openSettingsWindow());
        settingsMenu.add(settingsItem);
        menuBar.add(settingsMenu);

        setJMenuBar(menuBar);
    }


    //  BOARD PANEL  (drag-and-drop + click-to-move)

    /**
     * Creates the 8x8 grid of square buttons with both click and drag support.
     *
     * @return the board panel
     */
    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                SquareButton btn = new SquareButton(row, col);
                btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, squareSize / 2));
                btn.setPreferredSize(new Dimension(squareSize, squareSize));
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                btn.setFocusPainted(false);
                setSquareColor(btn, row, col);

                // Click-to-move
                btn.addActionListener(e ->
                        handleSquareClick(btn.getBoardRow(), btn.getBoardCol()));

                // Drag-and-drop mouse listeners
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    /** Records the drag source when mouse is pressed on a friendly piece. */
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        int r = btn.getBoardRow();
                        int c = btn.getBoardCol();
                        Piece p = board.getPiece(r, c);
                        if (p != null && p.getColor().equals(currentTurn)) {
                            dragFromRow = r;
                            dragFromCol = c;
                        }
                    }

                    /** Executes the move when the mouse is released over a target square. */
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        if (dragFromRow < 0) return;

                        // Find which square the cursor is over
                        Point onScreen = btn.getLocationOnScreen();
                        int absX = onScreen.x + e.getX();
                        int absY = onScreen.y + e.getY();

                        int toRow = -1, toCol = -1;
                        outer:
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                Point loc = buttons[r][c].getLocationOnScreen();
                                int   bw  = buttons[r][c].getWidth();
                                int   bh  = buttons[r][c].getHeight();
                                if (absX >= loc.x && absX < loc.x + bw
                                        && absY >= loc.y && absY < loc.y + bh) {
                                    toRow = r;
                                    toCol = c;
                                    break outer;
                                }
                            }
                        }

                        if (toRow >= 0 && (toRow != dragFromRow || toCol != dragFromCol)) {
                            selectedPosition = new Position(dragFromRow, dragFromCol);
                            executeMove(dragFromRow, dragFromCol, toRow, toCol);
                        }

                        dragFromRow = -1;
                        dragFromCol = -1;
                    }
                });

                buttons[row][col] = btn;
                boardPanel.add(btn);
            }
        }

        return boardPanel;
    }


    //  SIDE PANEL  (history, captured pieces, undo button)

    /**
     * Builds the side panel containing the turn label, captured-piece rows,
     * move history text area, Undo button, and New Game button.
     *
     * @return the configured side panel
     */
    private JPanel createSidePanel() {
        JPanel side = new JPanel(new BorderLayout(5, 5));
        JPanel side = sidePanel;
        side.setPreferredSize(new Dimension(240, squareSize * 8));
        side.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        statusLabel = new JLabel("Turn: White", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 6, 0));
        side.add(statusLabel, BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        centre.add(makeCapturedSection("Black captured:", true));
        centre.add(Box.createVerticalStrut(4));
        centre.add(makeCapturedSection("White captured:", false));
        centre.add(Box.createVerticalStrut(8));

        JLabel histLabel = new JLabel("Move History");
        histLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        histLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centre.add(histLabel);

        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(moveHistoryArea);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(222, 300));
        scroll.setMaximumSize(new Dimension(222, 300));
        centre.add(scroll);

        side.add(centre, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        JButton undoBtn  = new JButton("⟵ Undo");
        JButton resetBtn = new JButton("↺ New Game");
        undoBtn.addActionListener(e  -> undoMove());
        resetBtn.addActionListener(e -> resetGame());
        btnPanel.add(undoBtn);
        btnPanel.add(resetBtn);
        side.add(btnPanel, BorderLayout.SOUTH);

        return side;
    }

    /**
     * Creates a labelled panel that will show captured pieces for one player.
     *
     * @param label    section heading
     * @param forBlack true = this row holds pieces Black has captured
     * @return the wrapper panel
     */
    private JPanel makeCapturedSection(String label, boolean forBlack) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(label);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(title);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        row.setPreferredSize(new Dimension(222, 34));
        row.setMaximumSize(new Dimension(222, 34));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (forBlack) {
            blackCapturedPanel = row;
        } else {
            whiteCapturedPanel = row;
        }

        wrapper.add(row);
        return wrapper;
    }


    //  CLICK-TO-MOVE

    /**
     * Handles a square click: first click selects a piece,
     * second click executes the move.
     *
     * @param row clicked row
     * @param col clicked column
     */
    private void handleSquareClick(int row, int col) {
        Piece clicked = board.getPiece(row, col);

        if (selectedPosition == null) {
            if (clicked == null) return;
            if (!clicked.getColor().equals(currentTurn)) {
                JOptionPane.showMessageDialog(this,
                        "It is " + capitalize(currentTurn) + "'s turn.");
                return;
            }
            selectedPosition = new Position(row, col);
            highlightSelected(row, col);
            return;
        }

        int fromRow = selectedPosition.getRow();
        int fromCol = selectedPosition.getCol();

        if (fromRow == row && fromCol == col) {
            selectedPosition = null;
            refreshBoard();
            return;
        }

        if (clicked != null && clicked.getColor().equals(currentTurn)) {
            selectedPosition = new Position(row, col);
            refreshBoard();
            highlightSelected(row, col);
            return;
        }

        executeMove(fromRow, fromCol, row, col);
    }


    //  SHARED MOVE EXECUTION

    /**
     * Executes a move, recording history and handling captures and king detection.
     *
     * @param fromRow source row
     * @param fromCol source column
     * @param toRow   destination row
     * @param toCol   destination column
     */
    private void executeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece moving = board.getPiece(fromRow, fromCol);
        if (moving == null || !moving.getColor().equals(currentTurn)) {
            selectedPosition = null;
            refreshBoard();
            return;
        }

        Piece target = board.getPiece(toRow, toCol);

        if (target != null && target.getColor().equals(currentTurn)) {
            selectedPosition = null;
            refreshBoard();
            return;
        }

        // Save full state for undo
        boardHistory.push(board.copyBoard());
        turnHistory.push(currentTurn);
        capturedHistory.push(snapshotCaptured());

        // Build move history entry
        String moveText = capitalize(currentTurn) + ": "
                + toChessNotation(fromRow, fromCol) + " → "
                + toChessNotation(toRow, toCol);

        if (target != null) {
            moveText += "  ✕" + getPieceUnicode(target);
            if (currentTurn.equals("white")) {
                whiteCaptured.add(getPieceUnicode(target));
            } else {
                blackCaptured.add(getPieceUnicode(target));
            }
            refreshCapturedPanels();
        }

        boolean capturedKing = target instanceof King;

        board.movePiece(new Position(fromRow, fromCol), new Position(toRow, toCol));
        moveHistoryArea.append(moveText + "\n");
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());

        selectedPosition = null;
        refreshBoard();

        if (capturedKing) {
            JOptionPane.showMessageDialog(this,
                    "♛  " + capitalize(currentTurn) + " wins by capturing the King!",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        switchTurn();
    }


    //  UNDO  


    /**
     * Reverts the game to the state before the last move,
     * restoring the board, captured pieces, and move history.
     */
    private void undoMove() {
        if (boardHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No moves to undo.");
            return;
        }

        board       = boardHistory.pop();
        currentTurn = turnHistory.pop();
        String[] snap = capturedHistory.pop();

        whiteCaptured = decodeCaptures(snap[0]);
        blackCaptured = decodeCaptures(snap[1]);

        selectedPosition = null;
        refreshBoard();
        refreshCapturedPanels();
        statusLabel.setText("Turn: " + capitalize(currentTurn));

        // Strip last line from move history
        String text   = moveHistoryArea.getText();
        int    lastNL = text.lastIndexOf('\n', text.length() - 2);
        moveHistoryArea.setText(lastNL >= 0 ? text.substring(0, lastNL + 1) : "");
    }

    //  SAVE GAME

    /**
     * Saves the current game state to a .chess file chosen by the user.
     * The file stores turn, move history, captured pieces, and board layout.
     */
    private void saveGame() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Game");
        fc.setFileFilter(new FileNameExtensionFilter("Chess save (*.chess)", "chess"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".chess")) {
            file = new File(file.getAbsolutePath() + ".chess");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("TURN:" + currentTurn);
            pw.println("HISTORY:" + moveHistoryArea.getText().replace("\n", "\\n"));
            pw.println("WHITE_CAPTURED:" + String.join("\u001F", whiteCaptured));
            pw.println("BLACK_CAPTURED:" + String.join("\u001F", blackCaptured));

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece p = board.getPiece(r, c);
                    if (p == null) {
                        pw.println("EMPTY");
                    } else {
                        pw.println(p.getClass().getSimpleName()
                                + ":" + p.getColor() + ":" + r + ":" + c);
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Game saved to:\n" + file.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //  LOAD GAME

    /**
     * Loads a game from a .chess file chosen by the user,
     * restoring turn, history, captured pieces, and board state.
     */
    private void loadGame() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load Game");
        fc.setFileFilter(new FileNameExtensionFilter("Chess save (*.chess)", "chess"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
            currentTurn = br.readLine().replace("TURN:", "");

            String histLine = br.readLine().replace("HISTORY:", "");
            moveHistoryArea.setText(histLine.replace("\\n", "\n"));

            whiteCaptured = decodeCaptures(br.readLine().replace("WHITE_CAPTURED:", ""));
            blackCaptured = decodeCaptures(br.readLine().replace("BLACK_CAPTURED:", ""));

            Board loaded = new Board();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("EMPTY")) continue;
                String[] parts = line.split(":");
                int r = Integer.parseInt(parts[2]);
                int c = Integer.parseInt(parts[3]);
                loaded.setPiece(r, c, createPiece(parts[0], parts[1], new Position(r, c)));
            }

            board = loaded;
            boardHistory.clear();
            turnHistory.clear();
            capturedHistory.clear();
            selectedPosition = null;

            refreshBoard();
            refreshCapturedPanels();
            statusLabel.setText("Turn: " + capitalize(currentTurn));
            JOptionPane.showMessageDialog(this, "Game loaded successfully.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Load failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reconstructs a Piece from its class name, colour, and position.
     * Used when reading a saved game file.
     *
     * @param type  simple class name (e.g. "Queen")
     * @param color piece colour string
     * @param pos   board position
     * @return the appropriate Piece subclass instance
     */
    private Piece createPiece(String type, String color, Position pos) {
        return switch (type) {
            case "Pawn"   -> new pieces.Pawn(color, pos);
            case "Rook"   -> new pieces.Rook(color, pos);
            case "Knight" -> new pieces.Knight(color, pos);
            case "Bishop" -> new pieces.Bishop(color, pos);
            case "Queen"  -> new pieces.Queen(color, pos);
            case "King"   -> new pieces.King(color, pos);
            default       -> throw new IllegalArgumentException("Unknown piece: " + type);
        };
    }

    //  SETTINGS WINDOW

    /**
     * Opens a modal Settings dialog allowing the user to choose board colours
     * (via preset themes or custom colour pickers) and board size.
     * Clicking Apply updates the board immediately.
     */
    private void openSettingsWindow() {
        JDialog dialog = new JDialog(this, "Board & Piece Settings", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(380, 260);
        dialog.setLocationRelativeTo(this);
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));

        // Theme preset
        form.add(new JLabel("Board theme:"));
        String[] themes = {"Classic Wood", "Modern Gray", "Green Felt", "Blue Ocean", "Custom"};
        JComboBox<String> themeBox = new JComboBox<>(themes);
        form.add(themeBox);

        // Custom light colour
        form.add(new JLabel("Light square colour:"));
        JButton lightBtn = new JButton("Choose…");
        lightBtn.setBackground(lightSquareColor);
        final Color[] chosenLight = {lightSquareColor};
        lightBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(dialog, "Light Square", chosenLight[0]);
            if (c != null) { chosenLight[0] = c; lightBtn.setBackground(c);
                             themeBox.setSelectedItem("Custom"); }
        });
        form.add(lightBtn);

        // Custom dark colour
        form.add(new JLabel("Dark square colour:"));
        JButton darkBtn = new JButton("Choose…");
        darkBtn.setBackground(darkSquareColor);
        final Color[] chosenDark = {darkSquareColor};
        darkBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(dialog, "Dark Square", chosenDark[0]);
            if (c != null) { chosenDark[0] = c; darkBtn.setBackground(c);
                             themeBox.setSelectedItem("Custom"); }
        });
        form.add(darkBtn);

        // Board size
        form.add(new JLabel("Board size:"));
        String[] sizes = {"Small (60 px)", "Medium (80 px)", "Large (100 px)"};
        JComboBox<String> sizeBox = new JComboBox<>(sizes);
        sizeBox.setSelectedIndex(squareSize == 60 ? 0 : squareSize == 100 ? 2 : 1);
        form.add(sizeBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> {
            // Apply preset theme colours unless "Custom" was selected
            switch ((String) themeBox.getSelectedItem()) {
                case "Classic Wood" ->
                    { chosenLight[0] = new Color(240,217,181); chosenDark[0] = new Color(181,136, 99); }
                case "Modern Gray"  ->
                    { chosenLight[0] = new Color(200,200,200); chosenDark[0] = new Color(100,100,100); }
                case "Green Felt"   ->
                    { chosenLight[0] = new Color(238,238,210); chosenDark[0] = new Color( 84,139, 85); }
                case "Blue Ocean"   ->
                    { chosenLight[0] = new Color(173,216,230); chosenDark[0] = new Color( 32, 88,144); }
                default -> { /* keep custom picks */ }
            }

            lightSquareColor = chosenLight[0];
            darkSquareColor  = chosenDark[0];

            int[] px = {60, 80, 100};
            squareSize = px[sizeBox.getSelectedIndex()];

            applySettings();
            dialog.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnRow.add(applyBtn);
        btnRow.add(cancelBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Applies the current colour and size settings to every board square
     * and repacks the window so layout adjusts to the new square size.
     */
    private void applySettings() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                SquareButton btn = buttons[r][c];
                btn.setPreferredSize(new Dimension(squareSize, squareSize));
                btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, squareSize / 2));
                setSquareColor(btn, r, c);
            }
        }
        sidePanel.setPreferredSize(new Dimension(240, squareSize * 8));
        refreshBoard();
        pack();
    }


    //  RESET

    /**
     * Resets the game to its initial state: fresh board, cleared history,
     * empty captured panels, and White to move.
     */
    private void resetGame() {
        board = new Board();
        board.initializeBoard();
        selectedPosition = null;
        currentTurn      = "white";
        boardHistory.clear();
        turnHistory.clear();
        capturedHistory.clear();
        moveHistoryArea.setText("");
        whiteCaptured.clear();
        blackCaptured.clear();
        refreshCapturedPanels();
        statusLabel.setText("Turn: White");
        refreshBoard();
    }

    //  RENDERING HELPERS

    /**
     * Repaints every square with the correct piece symbol and background colour.
     */
    private void refreshBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                SquareButton btn   = buttons[r][c];
                Piece        piece = board.getPiece(r, c);
                setSquareColor(btn, r, c);
                btn.setText(piece == null ? "" : getPieceUnicode(piece));
            }
        }
        repaint();
    }

    /**
     * Highlights the selected square yellow; all other squares use normal colours.
     *
     * @param row selected row
     * @param col selected column
     */
    private void highlightSelected(int row, int col) {
        refreshBoard();
        buttons[row][col].setBackground(new Color(255, 220, 50));
    }

    /**
     * Sets a button's background to the current light or dark square colour.
     *
     * @param btn the square button to colour
     * @param row the button's row
     * @param col the button's column
     */
    private void setSquareColor(SquareButton btn, int row, int col) {
        btn.setBackground((row + col) % 2 == 0 ? lightSquareColor : darkSquareColor);
    }

    /**
     * Redraws both captured-piece display panels from the current lists.
     */
    private void refreshCapturedPanels() {
        updateCapturedPanel(whiteCapturedPanel, whiteCaptured);
        updateCapturedPanel(blackCapturedPanel, blackCaptured);
    }

    /**
     * Repopulates a single captured-piece panel from a list of Unicode symbols.
     *
     * @param panel  the panel to update
     * @param pieces list of piece symbols
     */
    private void updateCapturedPanel(JPanel panel, List<String> pieces) {
        panel.removeAll();
        for (String sym : pieces) {
            JLabel lbl = new JLabel(sym);
            lbl.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
            panel.add(lbl);
        }
        panel.revalidate();
        panel.repaint();
    }
   //  HELPERS


    /**
     * Switches the active player and updates the turn label.
     */
    private void switchTurn() {
        currentTurn = currentTurn.equals("white") ? "black" : "white";
        statusLabel.setText("Turn: " + capitalize(currentTurn));
    }

    /**
     * Converts a board position to standard algebraic notation (e.g. E4).
     *
     * @param row board row (0 = rank 8)
     * @param col board column (0 = file A)
     * @return notation string like "E4"
     */
    private String toChessNotation(int row, int col) {
        return "" + (char) ('A' + col) + (8 - row);
    }

    /**
     * Returns the Unicode chess symbol for a piece.
     *
     * @param piece the piece to look up
     * @return a single Unicode character string (e.g. "♔")
     */
    private String getPieceUnicode(Piece piece) {
        boolean w = piece.getColor().equals("white");
        return switch (piece.getClass().getSimpleName()) {
            case "King"   -> w ? "♔" : "♚";
            case "Queen"  -> w ? "♕" : "♛";
            case "Rook"   -> w ? "♖" : "♜";
            case "Bishop" -> w ? "♗" : "♝";
            case "Knight" -> w ? "♘" : "♞";
            case "Pawn"   -> w ? "♙" : "♟";
            default       -> piece.getSymbol();
        };
    }

    /**
     * Capitalises the first character of a string.
     *
     * @param s input string
     * @return string with first letter uppercased
     */
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Snapshots the current captured-piece lists into a two-element String array
     * so they can be pushed onto the undo stack.
     *
     * @return [white captured encoded, black captured encoded]
     */
    private String[] snapshotCaptured() {
        return new String[]{
            String.join("\u001F", whiteCaptured),
            String.join("\u001F", blackCaptured)
        };
    }

    /**
     * Decodes a captured-piece snapshot string back into a mutable list.
     *
     * @param encoded the encoded string (unit-separator delimited)
     * @return mutable list of piece symbols
     */
    private List<String> decodeCaptures(String encoded) {
        List<String> list = new ArrayList<>();
        if (encoded != null && !encoded.isEmpty()) {
            for (String s : encoded.split("\u001F")) {
                list.add(s);
            }
        }
        return list;
    }
}

