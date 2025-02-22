package org.example.ludoo.Controller;

import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Token;
import org.example.ludoo.Service.GameService;
import org.example.ludoo.Service.TokenService;
import org.example.ludoo.Service.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
@RestController
@RequestMapping("/game")
public class TokenController {

    private final TokenService tokenService;
    private final GameService gameService;
    private final WebSocketService webSocketService;

    public TokenController(TokenService tokenService, GameService gameService, WebSocketService webSocketService) {
        this.tokenService = tokenService;
        this.gameService = gameService;
        this.webSocketService = webSocketService;
    }

    @PostMapping("/{gameId}/move/{playerId}/{tokenId}/{diceValue}")
    public ResponseEntity<String> moveToken(@PathVariable UUID gameId,
                                            @PathVariable UUID playerId,
                                            @PathVariable UUID tokenId,
                                            @PathVariable int diceValue) {
        if (!gameService.isPlayerTurn(gameId, playerId)) {
            return ResponseEntity.badRequest().body("Not the player's turn.");
        }

        if (!tokenService.hasValidMove(gameId, playerId, diceValue)) {
            return ResponseEntity.badRequest().body("No valid moves available.");
        }

        boolean moveSuccess = tokenService.moveToken(gameId, playerId, tokenId, diceValue);

        if (moveSuccess) {
            gameService.nextTurn(gameId);
            // Fetch the updated game state
            Optional<Game> updatedGame = gameService.getGameState(gameId);
            updatedGame.ifPresent(game -> webSocketService.notifyGameUpdate(gameId, game));

            return ResponseEntity.ok("Token moved successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid move.");
        }
    }
}