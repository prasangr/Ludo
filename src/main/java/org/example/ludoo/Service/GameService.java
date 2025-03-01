package org.example.ludoo.Service;


import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Repository.GameRepository;
import org.example.ludoo.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.Optional;
import java.util.UUID;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;


    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }
    private TokenService tokenService;

    @Autowired
    public void setTokenService(@Lazy TokenService tokenService) {
        this.tokenService = tokenService;
    }
    public Game createGame() {
        Game game = new Game();
        return gameRepository.save(game);
    }

    public Optional<Player> joinGame(UUID gameId,String playerName) {
        return gameRepository.findById(gameId).map(game -> {
            if (game.getPlayers().size()<4) {
                Player player = new Player(playerName,game);
                playerRepository.save(player);
                return player;
            } else {
                return null; // if more than 4 players
            }
        });
    }

    public Optional<Game> getGameState(UUID gameId) {
        return gameRepository.findById(gameId);
    }

    // Check if it's the player's turn and if they have a valid move
    public boolean isPlayerTurn(UUID gameId, UUID playerId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isPresent()) {
            Game game =gameOpt.get();
            List<Player> players = game.getPlayers();

            if (!players.isEmpty()) {
                Player currentPlayer = players.get(game.getCurrentTurn());
                if (currentPlayer.getId().equals(playerId)) {
                    // Check if player has a valid move
                    return tokenService.hasValidMove(gameId, playerId, -1); // -1 indicates checking for any valid move
                }
            }
        }
        return false;
    }
    public void nextTurn(UUID gameId) {
        gameRepository.findById(gameId).ifPresent(game -> {
            game.setCurrentTurn((game.getCurrentTurn() + 1) % game.getPlayers().size());
            gameRepository.save(game);
        });
    }


    // Check if a player has won
    public boolean checkWinner(Game game, Player player) {
        boolean hasWon = player.getTokens().stream()
                .allMatch(token -> token.getPosition() == 57);  // Check if all tokens reached home

        if (hasWon) {
            game.setWinner(player);
            game.setGameOver(true);
            gameRepository.save(game);
        }

        return hasWon;
    }

}
