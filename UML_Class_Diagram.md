# UML Class Diagram (Phase 3 Integration)

```mermaid
classDiagram
    class Main {
      +main(String[] args)
    }

    class Game {
      -Board board
      +Game()
      +start()
    }

    class ChessGUI {
      -Board board
      -SquareButton[][] buttons
      -Position selectedPosition
      -String currentTurn
      +ChessGUI(Board board)
      -handleSquareClick(int row, int col)
      -executeMove(int fromRow, int fromCol, int toRow, int toCol)
      -evaluateGameStateAfterTurnSwitch()
      -undoMove()
      -saveGame()
      -loadGame()
      -refreshBoard()
    }

    class SquareButton {
      -int boardRow
      -int boardCol
      +SquareButton(int row, int col)
      +getBoardRow() int
      +getBoardCol() int
    }

    class Board {
      -Piece[][] grid
      +initializeBoard()
      +getPiece(int row, int col) Piece
      +setPiece(int row, int col, Piece piece)
      +movePiece(Position from, Position to)
      +isLegalMove(Position from, Position to, String movingColor) boolean
      +isInCheck(String color) boolean
      +hasAnyLegalMove(String color) boolean
      +isCheckmate(String color) boolean
      +isStalemate(String color) boolean
      +copyBoard() Board
    }

    class Position {
      -int row
      -int col
      +Position(int row, int col)
      +getRow() int
      +getCol() int
      +setRow(int row)
      +setCol(int col)
    }

    class Piece {
      <<abstract>>
      #String color
      #Position position
      #String symbol
      +getPossibleMoves(Board board) List~Position~
    }

    class King
    class Queen
    class Rook
    class Bishop
    class Knight
    class Pawn

    Main --> Game
    Game --> Board
    Game --> ChessGUI
    ChessGUI --> Board
    ChessGUI --> SquareButton
    Board --> Piece
    Piece --> Position
    Board --> Position

    Piece <|-- King
    Piece <|-- Queen
    Piece <|-- Rook
    Piece <|-- Bishop
    Piece <|-- Knight
    Piece <|-- Pawn
```

## Notes
- `ChessGUI` is now fully integrated with backend validation via `Board.isLegalMove(...)`.
- `Board` centralizes rule checks for move legality, check, checkmate, and stalemate.
- Piece classes remain responsible for movement patterns, while `Board` enforces king safety.
