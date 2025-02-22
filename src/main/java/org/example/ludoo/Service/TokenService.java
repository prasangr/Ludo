package org.example.ludoo.Service;


import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Models.Token;
import org.example.ludoo.Repository.GameRepository;
import org.example.ludoo.Repository.PlayerRepository;
import org.example.ludoo.Repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final GameRepository gameRepository;
    private final WebSocketService webSocketService;
    private final GameService gameService;

    public TokenService(TokenRepository tokenRepository, GameRepository gameRepository, WebSocketService webSocketService, GameService gameService) {
        this.tokenRepository = tokenRepository;
        this.gameRepository = gameRepository;
        this.webSocketService = webSocketService;
        this.gameService = gameService;
    }

    // Define safe spots
    private static final List<Integer> SAFE_SPOTS = List.of(0, 8, 13, 21, 26, 34, 39, 47, 52);

    // Check if the player has a valid move
    public boolean hasValidMove(UUID gameId, UUID playerId, int diceValue) {
        List<Token> playerTokens = tokenRepository.findByPlayerId(playerId);
        return playerTokens.stream().anyMatch(token -> token.getPosition() + diceValue <= 57);
    }

    // Move a token based on the dice roll
    public boolean moveToken(UUID gameId, UUID playerId, UUID tokenId, int diceValue) {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        Optional<Game> gameOpt = gameRepository.findById(gameId);

        if (tokenOpt.isPresent() && gameOpt.isPresent()) {
            Token token = tokenOpt.get();
            Game game = gameOpt.get();
            Player player = token.getPlayer();

            if (token.getPosition() == 0 && diceValue != 6) {
                return false; // Cannot move unless dice roll is 6
            }

            int newPosition = token.getPosition() + diceValue;

            List<Token> tokensAtNewPosition = tokenRepository.findByPosition(newPosition);
            for (Token otherToken : tokensAtNewPosition) {
                if (!otherToken.getPlayer().getId().equals(player.getId()) && !SAFE_SPOTS.contains(newPosition)) {
                    otherToken.setPosition(0);
                    tokenRepository.save(otherToken);
                }
            }

            if (newPosition <= 57) {
                token.setPosition(newPosition);
                tokenRepository.save(token);
            }

            boolean hasWon = player.getTokens().stream().allMatch(t -> t.getPosition() == 57);
            if (hasWon) {
                game.setWinner(player);
                game.setGameOver(true);
                gameRepository.save(game);
            }

            // **Notify all players about the move via WebSockets**
            webSocketService.notifyGameUpdate(gameId, game);

            return true;
        }
        return false;
    }
}

