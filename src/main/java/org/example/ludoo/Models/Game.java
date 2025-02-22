package org.example.ludoo.Models;


import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private boolean isGameOver = false;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players;

    private int currentTurn = 0; // Index of the player whose turn it is
    @OneToOne
    private Player winner;  // Stores the winning player


}