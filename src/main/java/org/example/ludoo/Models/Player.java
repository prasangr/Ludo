package org.example.ludoo.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "game_id")
    @JsonBackReference
    private Game game;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Token> tokens;

    public Player() {
     //default for jpa
    }

    public Player(String name, Game game) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.game = game;
        this.tokens = initializeTokens();
    }

    private List<Token> initializeTokens() {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Token token = new Token(this);
            token.setPosition(-1);
            tokens.add(token);
        }
        return tokens;
    }
}