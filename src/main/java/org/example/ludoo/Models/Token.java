// Token.java
    package org.example.ludoo.Models;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.Data;
    import java.util.UUID;

    @Entity
    @Data
    public class Token {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        private int position = 0;

        @ManyToOne
        @JoinColumn(name = "player_id")
        @JsonBackReference
        private Player player;

        public Token() {
            // Default constructor for JPA
        }

        public Token(Player player) {
            this.id = UUID.randomUUID();
            this.player = player;
            this.position = 0;
        }
    }