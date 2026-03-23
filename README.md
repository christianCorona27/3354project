# 3354project - Chess Backend

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
├── game/       Game loop and input handling
├── pieces/     Abstract Piece class and all six subclasses
├── player/     Player data
└── utils/      Position helper class

## How to Compile
Run this from the project root directory:
bashmkdir -p out
javac -d out src/Main.java src/board/*.java src/game/*.java src/pieces/*.java src/player/*.java src/utils/*.java

## How to Run
  bashjava -cp out Main


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
- text-based board display
- move input parsing
- turn switching
- basic movement validation for pieces


## Not Yet Implemented
- checkmate/ stalemate detection
- check detection
- castling
- en passant
- pawn promotion
- Full Game-Over conditions

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
