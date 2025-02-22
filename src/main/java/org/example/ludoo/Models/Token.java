package org.example.ludoo.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int position = 0; // Track token position on the board

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
}