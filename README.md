# 3354project - Chess Backend

## Overview
This project implements Phase 1 of a Java command-line chess backend.

The program displays a text-based chess board, initializes the starting pieces,
accepts moves in coordinate format, updates the board, and switches
turns between white and black.

## Requirements
- Java 17 or later
- Git Bash, terminal, or any Java IDE

## Project Structure
- `src/board` - board representation and board operations
- `src/game` - game loop and move input handling
- `src/pieces` - chess piece superclass and subclasses
- `src/player` - player data
- `src/utils` - helper classes such as board positions

## How to Compile
Run this from the project root:

Features Implemented
- 8x8 board representation
- initial chess board setup
- abstract Piece superclass
- separate piece subclasses
- text-based board display
- move input parsing
- turn switching
- basic movement validation for pieces


Not Yet Implemented
- check
- checkmate
- castling
- en passant
- pawn promotion
- full game-over conditions

```bash
mkdir -p out
javac -d out src/Main.java src/board/*.java src/game/*.java src/pieces/*.java src/player/*.java src/utils/*.java

## How to Run
java -cp out Main

##Controls
Enter moves in this format:
Ex : E2 E4
to exit : quit
