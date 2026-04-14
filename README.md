# 3354project - Chess Game Gui

## Overview
This project implements Phase 1 of a Java command-line chess backend.
A console-based chess game written in Java. Two players take turns entering moves
in standard chess notation. The board is displayed after every move, and the game
validates each move before accepting it.

## Requirements
- Java 17 or later
- Git Bash, terminal, or any Java IDE

## Project Structure
src/
├── Main.java

├── board/      Board representation and move execution

├── game/       Game logic

├── gui/        GUI classes for board display and interaction

├── pieces/     Abstract Piece class and piece subclasses

├── player/     Player data

└── utils/      Position helper class

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
Enter moves in the format [FROM] [TO] using standard chess notation:
E2 E4     moves the piece at E2 to E4
A2 A4     moves the pawn forward two squares
Type quit at any time to exit the game.

## Features Implemented
- 8x8 board representation
- initial chess board setup
- abstract Piece superclass
- separate piece subclasses
- move validation
- turn switching

## NEW (Phase 2 GUI)
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

Example Session
   
   A   B   C   D   E   F   G   H

8  bR  bN  bB  bQ  bK  bB  bN  bR

7  bp  bp  bp  bp  bp  bp  bp  bp

6  ##  ##  ##  ##  ##  ##  ##  ##

5  ##  ##  ##  ##  ##  ##  ##  ##

4  ##  ##  ##  ##  ##  ##  ##  ##

3  ##  ##  ##  ##  ##  ##  ##  ##

2  wp  wp  wp  wp  wp  wp  wp  wp

1  wR  wN  wB  wQ  wK  wB  wN  wR

white's turn.
Enter move (example: E2 E4) or type quit: E2 E4
