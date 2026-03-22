package player;

import java.util.ArrayList;
import java.util.List;
import pieces.Piece;

/**
 * Represents one player in the game.
 */
public class Player {

    /** Player name. */
    private String name;

    /** Player color. */
    private String color;

    /** Pieces that still belong to this player. */
    private List<Piece> activePieces;

    /**
     * Creates a player with a name and color.
     *
     * @param name player name
     * @param color player color
     */
    public Player(String name, String color) {
        this.name = name;
        this.color = color;
        this.activePieces = new ArrayList<>();
    }

    /**
     * Gets the player's name.
     *
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's color.
     *
     * @return player color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the player's remaining pieces.
     *
     * @return list of active pieces
     */
    public List<Piece> getActivePieces() {
        return activePieces;
    }

    /**
     * Adds a piece to the player's list.
     *
     * @param piece piece to add
     */
    public void addPiece(Piece piece) {
        activePieces.add(piece);
    }

    /**
     * Removes a piece after it is captured.
     *
     * @param piece piece to remove
     */
    public void removePiece(Piece piece) {
        activePieces.remove(piece);
    }
}
