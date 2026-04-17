package gui;

import board.Board;
import pieces.*;
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
 * captured pieces, undo, save/load game, settings customisation,
 * check/checkmate/stalemate detection, pawn promotion, castling, and en passant.
 */
public class ChessGUI extends JFrame {

    // ── Board state ──────────────────────────────────────────────────────
    private Board board;
    private SquareButton[][] buttons;
    private Position selectedPosition;
    private String currentTurn;

    // ── Undo stacks ──────────────────────────────────────────────────────
    private Stack<Board> boardHistory;
    private Stack<String> turnHistory;
    private Stack<String[]> capturedHistory;

    // ── Side-panel widgets ───────────────────────────────────────────────
    private JLabel statusLabel;
    private JTextArea moveHistoryArea;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private JPanel sidePanel;

    // ── Settings ─────────────────────────────────────────────────────────
    private Color lightSquareColor   = new Color(240, 217, 181);
    private Color darkSquareColor    = new Color(181, 136,  99);
    private Color highlightMoveColor = new Color(100, 200, 100);   // legal move dot
    private Color checkColor         = new Color(220,  60,  60);   // king in check
    private int   squareSize         = 80;

    // ── Drag state ───────────────────────────────────────────────────────
    private int dragFromRow = -1;
    private int dragFromCol = -1;

    // ── Captured piece lists ─────────────────────────────────────────────
    private List<String> whiteCaptured = new ArrayList<>();
    private List<String> blackCaptured = new ArrayList<>();

    // ── Legal moves for the currently selected piece ─────────────────────
    private List<Position> currentLegalMoves = new ArrayList<>();

    /**
     * Creates the chess GUI window and starts the game.
     *
     * @param board the initial game board
     */
    public ChessGUI(Board board) {
        this.board            = board;
        this.buttons          = new SquareButton[8][8];
        this.selectedPosition = null;
        this.currentTurn      = "white";
        this.boardHistory     = new Stack<>();
        this.turnHistory      = new Stack<>();
        this.capturedHistory  = new Stack<>();

        setTitle("Chess Game – Phase 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        createMenuBar();
        add(createBoardPanel(), BorderLayout.CENTER);
        sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        refreshBoard();
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════
    //  MENU BAR
    // ════════════════════════════════════════════════════════════════════

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu   = new JMenu("Game");

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

        JMenu settingsMenu  = new JMenu("Settings");
        JMenuItem styleItem = new JMenuItem("Board & Piece Style…");
        styleItem.addActionListener(e -> openSettingsWindow());
        settingsMenu.add(styleItem);
        menuBar.add(settingsMenu);

        setJMenuBar(menuBar);
    }

    // ════════════════════════════════════════════════════════════════════
    //  BOARD PANEL  (drag-and-drop + click-to-move)
    // ════════════════════════════════════════════════════════════════════

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

                btn.addActionListener(e ->
                        handleSquareClick(btn.getBoardRow(), btn.getBoardCol()));

                btn.addMouseListener(new java.awt.event.MouseAdapter() {
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

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        if (dragFromRow < 0) return;
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
                                    toRow = r; toCol = c;
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

    // ════════════════════════════════════════════════════════════════════
    //  SIDE PANEL
    // ════════════════════════════════════════════════════════════════════

    private JPanel createSidePanel() {
        JPanel side = new JPanel(new BorderLayout(5, 5));
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

        if (forBlack) blackCapturedPanel = row;
        else          whiteCapturedPanel = row;

        wrapper.add(row);
        return wrapper;
    }

    // ════════════════════════════════════════════════════════════════════
    //  CLICK-TO-MOVE
    // ════════════════════════════════════════════════════════════════════

    private void handleSquareClick(int row, int col) {
        Piece clicked = board.getPiece(row, col);

        if (selectedPosition == null) {
            if (clicked == null) return;
            if (!clicked.getColor().equals(currentTurn)) {
                JOptionPane.showMessageDialog(this,
                        "It is " + capitalize(currentTurn) + "'s turn.");
                return;
            }
            selectedPosition   = new Position(row, col);
            currentLegalMoves  = getLegalMoves(clicked, board);
            highlightSelected(row, col);
            return;
        }

        int fromRow = selectedPosition.getRow();
        int fromCol = selectedPosition.getCol();

        if (fromRow == row && fromCol == col) {
            selectedPosition  = null;
            currentLegalMoves = new ArrayList<>();
            refreshBoard();
            return;
        }

        if (clicked != null && clicked.getColor().equals(currentTurn)) {
            selectedPosition  = new Position(row, col);
            currentLegalMoves = getLegalMoves(clicked, board);
            refreshBoard();
            highlightSelected(row, col);
            return;
        }

        executeMove(fromRow, fromCol, row, col);
    }

    // ════════════════════════════════════════════════════════════════════
    //  LEGAL MOVE COMPUTATION  (filters out moves that leave king in check)
    // ════════════════════════════════════════════════════════════════════

    /**
     * Returns the fully-legal moves for a piece: raw candidates minus those
     * that leave the mover's own king in check. Also validates castling safety.
     *
     * @param piece piece to evaluate
     * @param b     board to evaluate on
     * @return filtered list of legal destination positions
     */
    private List<Position> getLegalMoves(Piece piece, Board b) {
        List<Position> raw  = piece.getPossibleMoves(b);
        List<Position> legal = new ArrayList<>();

        int fromRow = piece.getPosition().getRow();
        int fromCol = piece.getPosition().getCol();

        for (Position to : raw) {
            // For castling moves, also verify the king doesn't pass through check
            if (piece instanceof King && Math.abs(to.getCol() - fromCol) == 2) {
                if (!isCastlingLegal(piece.getColor(), to.getCol(), b)) continue;
            }

            // Simulate the move on a copy
            Board copy = b.copyBoard();
            Piece movingCopy = copy.getPiece(fromRow, fromCol);

            // Handle en passant capture removal on the copy
            if (movingCopy instanceof Pawn
                    && to.getCol() != fromCol
                    && copy.getPiece(to.getRow(), to.getCol()) == null) {
                // En passant: remove the captured pawn beside us
                int capturedPawnRow = fromRow; // same rank as moving pawn before it moves
                copy.setPiece(capturedPawnRow, to.getCol(), null);
            }

            copy.movePiece(new Position(fromRow, fromCol), to);

            if (!isInCheck(piece.getColor(), copy)) {
                legal.add(to);
            }
        }
        return legal;
    }

    /**
     * Returns true if the given side's king is in check on board b.
     *
     * @param color "white" or "black"
     * @param b     board to inspect
     * @return true if king is under attack
     */
    private boolean isInCheck(String color, Board b) {
        // Find this side's king
        int kingRow = -1, kingCol = -1;
        outer:
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b.getPiece(r, c);
                if (p instanceof King && p.getColor().equals(color)) {
                    kingRow = r; kingCol = c;
                    break outer;
                }
            }
        }
        if (kingRow < 0) return false; // shouldn't happen in a real game

        String opponent = color.equals("white") ? "black" : "white";

        // Check if any opponent piece can reach the king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b.getPiece(r, c);
                if (p != null && p.getColor().equals(opponent)) {
                    for (Position atk : p.getPossibleMoves(b)) {
                        if (atk.getRow() == kingRow && atk.getCol() == kingCol) return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Verifies that a castling move is legal:
     * the king must not be in check currently, must not pass through an
     * attacked square, and must not end in check.
     *
     * @param color     castling side
     * @param toCol     destination column (6 = king-side, 2 = queen-side)
     * @param b         current board
     * @return true if castling is fully legal
     */
    private boolean isCastlingLegal(String color, int toCol, Board b) {
        int backRank  = color.equals("white") ? 7 : 0;
        int kingCol   = 4;

        // King must not currently be in check
        if (isInCheck(color, b)) return false;

        // Determine the squares the king traverses
        int step = (toCol > kingCol) ? 1 : -1;
        int midCol = kingCol + step;

        // Mid square must not be attacked
        Board midCopy = b.copyBoard();
        midCopy.movePiece(new Position(backRank, kingCol), new Position(backRank, midCol));
        if (isInCheck(color, midCopy)) return false;

        // Destination must not be attacked
        Board dstCopy = b.copyBoard();
        dstCopy.movePiece(new Position(backRank, kingCol), new Position(backRank, toCol));
        if (isInCheck(color, dstCopy)) return false;

        return true;
    }

    /**
     * Returns true if the current player has no legal moves (checkmate or stalemate).
     *
     * @param color side to evaluate
     * @return true if the player has zero legal moves
     */
    private boolean hasNoLegalMoves(String color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor().equals(color)) {
                    if (!getLegalMoves(p, board).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    // ════════════════════════════════════════════════════════════════════
    //  SHARED MOVE EXECUTION
    // ════════════════════════════════════════════════════════════════════

    /**
     * Executes a move after validating legality, handles special moves
     * (castling, en passant, pawn promotion), updates GUI, checks for
     * check/checkmate/stalemate.
     *
     * @param fromRow source row
     * @param fromCol source column
     * @param toRow   destination row
     * @param toCol   destination column
     */
    private void executeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece moving = board.getPiece(fromRow, fromCol);
        if (moving == null || !moving.getColor().equals(currentTurn)) {
            selectedPosition  = null;
            currentLegalMoves = new ArrayList<>();
            refreshBoard();
            return;
        }

        Piece target = board.getPiece(toRow, toCol);

        if (target != null && target.getColor().equals(currentTurn)) {
            selectedPosition  = null;
            currentLegalMoves = new ArrayList<>();
            refreshBoard();
            return;
        }

        // Validate against pre-computed or freshly computed legal moves
        List<Position> legal = getLegalMoves(moving, board);
        boolean isLegal = false;
        for (Position p : legal) {
            if (p.getRow() == toRow && p.getCol() == toCol) { isLegal = true; break; }
        }
        if (!isLegal) {
            selectedPosition  = null;
            currentLegalMoves = new ArrayList<>();
            refreshBoard();
            return;
        }

        // Save full state for undo
        boardHistory.push(board.copyBoard());
        turnHistory.push(currentTurn);
        capturedHistory.push(snapshotCaptured());

        // Build move-history entry
        String moveText = capitalize(currentTurn) + ": "
                + toChessNotation(fromRow, fromCol) + " → "
                + toChessNotation(toRow, toCol);

        // ── En passant capture ────────────────────────────────────────
        boolean isEnPassant = false;
        if (moving instanceof Pawn && toCol != fromCol && target == null) {
            isEnPassant = true;
            // Captured pawn sits on the same rank as the moving pawn (before the move)
            Piece captured = board.getPiece(fromRow, toCol);
            if (captured != null) {
                moveText += "  ✕" + getPieceUnicode(captured) + " (en passant)";
                if (currentTurn.equals("white")) whiteCaptured.add(getPieceUnicode(captured));
                else                             blackCaptured.add(getPieceUnicode(captured));
                board.setPiece(fromRow, toCol, null); // remove the captured pawn
                refreshCapturedPanels();
            }
        }

        // ── Normal capture ────────────────────────────────────────────
        if (target != null && !isEnPassant) {
            moveText += "  ✕" + getPieceUnicode(target);
            if (currentTurn.equals("white")) whiteCaptured.add(getPieceUnicode(target));
            else                             blackCaptured.add(getPieceUnicode(target));
            refreshCapturedPanels();
        }

        // ── Castling rook movement ────────────────────────────────────
        boolean isCastling = (moving instanceof King && Math.abs(toCol - fromCol) == 2);
        if (isCastling) {
            int backRank = currentTurn.equals("white") ? 7 : 0;
            if (toCol == 6) { // king-side
                Piece rook = board.getPiece(backRank, 7);
                board.setPiece(backRank, 7, null);
                board.setPiece(backRank, 5, rook);
                if (rook != null) rook.setPosition(new Position(backRank, 5));
                moveText += " (O-O)";
            } else { // queen-side  toCol == 2
                Piece rook = board.getPiece(backRank, 0);
                board.setPiece(backRank, 0, null);
                board.setPiece(backRank, 3, rook);
                if (rook != null) rook.setPosition(new Position(backRank, 3));
                moveText += " (O-O-O)";
            }
        }

        // Execute the move on the real board
        board.movePiece(new Position(fromRow, fromCol), new Position(toRow, toCol));

        // ── Pawn promotion ────────────────────────────────────────────
        if (moving instanceof Pawn && (toRow == 0 || toRow == 7)) {
            Piece promoted = promptPromotion(currentTurn, new Position(toRow, toCol));
            board.setPiece(toRow, toCol, promoted);
            moveText += " =" + getPieceUnicode(promoted);
        }

        moveHistoryArea.append(moveText + "\n");
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());

        selectedPosition  = null;
        currentLegalMoves = new ArrayList<>();
        refreshBoard();
        switchTurn();

        // ── Post-move game-state checks ───────────────────────────────
        String opponent = currentTurn; // already switched
        if (hasNoLegalMoves(opponent)) {
            if (isInCheck(opponent, board)) {
                // Checkmate
                String winner = opponent.equals("white") ? "Black" : "White";
                refreshBoard(); // paint the board before dialog
                JOptionPane.showMessageDialog(this,
                        "♛  Checkmate!  " + winner + " wins!",
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
                resetGame();
            } else {
                // Stalemate
                JOptionPane.showMessageDialog(this,
                        "Stalemate! The game is a draw.",
                        "Draw", JOptionPane.INFORMATION_MESSAGE);
                resetGame();
            }
            return;
        }

        if (isInCheck(opponent, board)) {
            statusLabel.setText("Turn: " + capitalize(opponent) + "  ⚠ CHECK!");
            statusLabel.setForeground(Color.RED);
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  PAWN PROMOTION DIALOG
    // ════════════════════════════════════════════════════════════════════

    /**
     * Shows a dialog asking the player to choose a promotion piece.
     *
     * @param color  "white" or "black"
     * @param pos    position where the promoted piece will stand
     * @return the chosen Piece (default Queen if dialog closed)
     */
    private Piece promptPromotion(String color, Position pos) {
        String[] options = {"Queen ♕", "Rook ♖", "Bishop ♗", "Knight ♘"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose a piece to promote your pawn to:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        return switch (choice) {
            case 1  -> new Rook(color, pos);
            case 2  -> new Bishop(color, pos);
            case 3  -> new Knight(color, pos);
            default -> new Queen(color, pos);
        };
    }

    // ════════════════════════════════════════════════════════════════════
    //  UNDO
    // ════════════════════════════════════════════════════════════════════

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

        selectedPosition  = null;
        currentLegalMoves = new ArrayList<>();
        refreshBoard();
        refreshCapturedPanels();
        statusLabel.setText("Turn: " + capitalize(currentTurn));
        statusLabel.setForeground(Color.BLACK);

        String text   = moveHistoryArea.getText();
        int    lastNL = text.lastIndexOf('\n', text.length() - 2);
        moveHistoryArea.setText(lastNL >= 0 ? text.substring(0, lastNL + 1) : "");
    }

    // ════════════════════════════════════════════════════════════════════
    //  SAVE GAME
    // ════════════════════════════════════════════════════════════════════

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
            // Castling / en-passant flags
            pw.println("WK_MOVED:"  + board.isWhiteKingMoved());
            pw.println("BK_MOVED:"  + board.isBlackKingMoved());
            pw.println("WRA1_MOVED:" + board.isWhiteRookA1Moved());
            pw.println("WRH1_MOVED:" + board.isWhiteRookH1Moved());
            pw.println("BRA8_MOVED:" + board.isBlackRookA8Moved());
            pw.println("BRH8_MOVED:" + board.isBlackRookH8Moved());
            pw.println("EP_ROW:"    + board.getEnPassantRow());
            pw.println("EP_COL:"    + board.getEnPassantCol());

            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece p = board.getPiece(r, c);
                    if (p == null) pw.println("EMPTY");
                    else pw.println(p.getClass().getSimpleName()
                                + ":" + p.getColor() + ":" + r + ":" + c);
                }
            }
            JOptionPane.showMessageDialog(this, "Game saved to:\n" + file.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  LOAD GAME
    // ════════════════════════════════════════════════════════════════════

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

            // Read flags
            loaded.setWhiteKingMoved(Boolean.parseBoolean(br.readLine().replace("WK_MOVED:", "")));
            loaded.setBlackKingMoved(Boolean.parseBoolean(br.readLine().replace("BK_MOVED:", "")));
            loaded.setWhiteRookA1Moved(Boolean.parseBoolean(br.readLine().replace("WRA1_MOVED:", "")));
            loaded.setWhiteRookH1Moved(Boolean.parseBoolean(br.readLine().replace("WRH1_MOVED:", "")));
            loaded.setBlackRookA8Moved(Boolean.parseBoolean(br.readLine().replace("BRA8_MOVED:", "")));
            loaded.setBlackRookH8Moved(Boolean.parseBoolean(br.readLine().replace("BRH8_MOVED:", "")));
            int epRow = Integer.parseInt(br.readLine().replace("EP_ROW:", ""));
            int epCol = Integer.parseInt(br.readLine().replace("EP_COL:", ""));
            loaded.setEnPassant(epRow, epCol);

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
            selectedPosition  = null;
            currentLegalMoves = new ArrayList<>();

            refreshBoard();
            refreshCapturedPanels();
            statusLabel.setText("Turn: " + capitalize(currentTurn));
            statusLabel.setForeground(Color.BLACK);
            JOptionPane.showMessageDialog(this, "Game loaded successfully.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Load failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Piece createPiece(String type, String color, Position pos) {
        return switch (type) {
            case "Pawn"   -> new Pawn(color, pos);
            case "Rook"   -> new Rook(color, pos);
            case "Knight" -> new Knight(color, pos);
            case "Bishop" -> new Bishop(color, pos);
            case "Queen"  -> new Queen(color, pos);
            case "King"   -> new King(color, pos);
            default       -> throw new IllegalArgumentException("Unknown piece: " + type);
        };
    }

    // ════════════════════════════════════════════════════════════════════
    //  SETTINGS WINDOW
    // ════════════════════════════════════════════════════════════════════

    private void openSettingsWindow() {
        JDialog dialog = new JDialog(this, "Board & Piece Settings", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(380, 260);
        dialog.setLocationRelativeTo(this);
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));

        form.add(new JLabel("Board theme:"));
        String[] themes = {"Classic Wood", "Modern Gray", "Green Felt", "Blue Ocean", "Custom"};
        JComboBox<String> themeBox = new JComboBox<>(themes);
        form.add(themeBox);

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

        form.add(new JLabel("Board size:"));
        String[] sizes = {"Small (60 px)", "Medium (80 px)", "Large (100 px)"};
        JComboBox<String> sizeBox = new JComboBox<>(sizes);
        sizeBox.setSelectedIndex(squareSize == 60 ? 0 : squareSize == 100 ? 2 : 1);
        form.add(sizeBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn  = new JButton("Apply");
        JButton cancelBtn = new JButton("Cancel");

        applyBtn.addActionListener(e -> {
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
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnRow.add(applyBtn);
        btnRow.add(cancelBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

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

    // ════════════════════════════════════════════════════════════════════
    //  RESET
    // ════════════════════════════════════════════════════════════════════

    private void resetGame() {
        board = new Board();
        board.initializeBoard();
        selectedPosition  = null;
        currentTurn       = "white";
        currentLegalMoves = new ArrayList<>();
        boardHistory.clear();
        turnHistory.clear();
        capturedHistory.clear();
        moveHistoryArea.setText("");
        whiteCaptured.clear();
        blackCaptured.clear();
        refreshCapturedPanels();
        statusLabel.setText("Turn: White");
        statusLabel.setForeground(Color.BLACK);
        refreshBoard();
    }

    // ════════════════════════════════════════════════════════════════════
    //  RENDERING HELPERS
    // ════════════════════════════════════════════════════════════════════

    /**
     * Repaints every square with the correct piece symbol and background.
     * Highlights the king in red when in check.
     * Dims squares that are legal move targets with a green tint.
     */
    private void refreshBoard() {
        // Build set of legal move destinations for quick lookup
        boolean[][] isLegalTarget = new boolean[8][8];
        for (Position p : currentLegalMoves) {
            isLegalTarget[p.getRow()][p.getCol()] = true;
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                SquareButton btn   = buttons[r][c];
                Piece        piece = board.getPiece(r, c);
                setSquareColor(btn, r, c);

                if (isLegalTarget[r][c]) {
                    // Blend toward green for legal move squares
                    Color base = btn.getBackground();
                    btn.setBackground(new Color(
                        (base.getRed()   + highlightMoveColor.getRed())   / 2,
                        (base.getGreen() + highlightMoveColor.getGreen()) / 2,
                        (base.getBlue()  + highlightMoveColor.getBlue())  / 2));
                }

                btn.setText(piece == null ? "" : getPieceUnicode(piece));
            }
        }

        // Highlight king in check
        String[] sides = {"white", "black"};
        for (String side : sides) {
            if (isInCheck(side, board)) {
                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        Piece p = board.getPiece(r, c);
                        if (p instanceof King && p.getColor().equals(side)) {
                            buttons[r][c].setBackground(checkColor);
                        }
                    }
                }
            }
        }

        repaint();
    }

    /**
     * Highlights the selected square yellow and shows legal move targets.
     */
    private void highlightSelected(int row, int col) {
        refreshBoard();
        buttons[row][col].setBackground(new Color(255, 220, 50));
    }

    private void setSquareColor(SquareButton btn, int row, int col) {
        btn.setBackground((row + col) % 2 == 0 ? lightSquareColor : darkSquareColor);
    }

    private void refreshCapturedPanels() {
        updateCapturedPanel(whiteCapturedPanel, whiteCaptured);
        updateCapturedPanel(blackCapturedPanel, blackCaptured);
    }

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

    // ════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════════

    private void switchTurn() {
        currentTurn = currentTurn.equals("white") ? "black" : "white";
        statusLabel.setText("Turn: " + capitalize(currentTurn));
        statusLabel.setForeground(Color.BLACK);
    }

    private String toChessNotation(int row, int col) {
        return "" + (char) ('A' + col) + (8 - row);
    }

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

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String[] snapshotCaptured() {
        return new String[]{
            String.join("\u001F", whiteCaptured),
            String.join("\u001F", blackCaptured)
        };
    }

    private List<String> decodeCaptures(String encoded) {
        List<String> list = new ArrayList<>();
        if (encoded != null && !encoded.isEmpty()) {
            for (String s : encoded.split("\u001F")) list.add(s);
        }
        return list;
    }
}
