package org.example.ludoo.DTO;

import org.example.ludoo.Models.Player;

import java.util.List;

public class GameState {
    private List<Player> players;
    private int currentPlayerIndex;
    private int diceValue;

    public GameState(List<Player> players, int currentPlayerIndex, int diceValue) {
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.diceValue = diceValue;
    }

    // Getters and setters
    public List<Player> getPlayers() { return players; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public int getDiceValue() { return diceValue; }
}