package org.example.ludoo.Controller;


import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Create a new game
    @PostMapping("/start")
    public ResponseEntity<Game> createGame() {
        Game game = gameService.createGame();
        return ResponseEntity.ok(game);
    }

    // Join a game
    @PostMapping("/{gameId}/join")
    public ResponseEntity<Player> joinGame(@PathVariable UUID gameId, @RequestParam String playerName) {
        return gameService.joinGame(gameId, playerName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }


    // Get the current game state
    @GetMapping("/{gameId}/state")
    public ResponseEntity<?> getGameState(@PathVariable UUID gameId) {
        return gameService.getGameState(gameId)
                .map(game -> game.isGameOver() ? ResponseEntity.ok("Winner: " + game.getWinner().getName())
                        : ResponseEntity.ok(game))
                .orElse(ResponseEntity.notFound().build());
    }
}