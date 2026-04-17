# 3354project - Chess Game GUI


## Overview
This project is a Java chess game with a graphical user interface. It integrates backend chess logic with the GUI so players can interact with the board visually while the program validates moves and updates the game state.

The project includes board logic, piece classes, turn handling, move history, save/load support, undo functionality, and a GUI-based chess board.

## Requirements
- Java 17 or later
- Git Bash, terminal, or any Java IDE

## Project Structure
```text
src/
├── Main.java
├── board/      Board representation and move execution
├── game/       Game logic
├── gui/        GUI classes for board display and interaction
├── pieces/     Abstract Piece class and piece subclasses
├── player/     Player data
└── utils/      Position helper class
```

## How to Compile
Run this from the project root directory:

```bash
mkdir -p out
javac -d out src/Main.java src/board/*.java src/game/*.java src/gui/*.java src/pieces/*.java src/player/*.java src/utils/*.java
```

## How to Run
```bash
java -cp out Main
```

## How to Play
- Launch the application
- Click and move pieces using the GUI
- The board updates after each move
- Use the available controls for features such as undo, save, and load

## Features Implemented
- 8x8 board representation
- Initial chess board setup
- Abstract `Piece` superclass
- Separate piece subclasses
- Move validation
- Turn switching
- GUI chess board
- Piece interaction through the GUI
- Undo functionality
- Save game
- Load game
- Captured pieces display
- Unicode chess piece symbols
- Customizable board colors
- Adjustable square size
- Move history
- graphical chess board (GUI)
- drag-and-drop movement
- undo system
- save game
- load game
- captured pieces display
- unicode chess pieces
- customizable board colors
- adjustable square size

## NOT YET Implemented
- Check/checkmate/ stalemate detection
- Castling
- Enpassant
- Pawn Promotion

