package org.example.ludoo.Service;

import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Models.Token;
import org.example.ludoo.Repository.GameRepository;
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
    private static final List<Integer> SAFE_SPOTS = List.of(0,8,13,21,26,34,39,47,52);
    private static final List<Integer> STARTING_POSITIONS = List.of(0,13,26,39);
    private static final List<Integer> HOME_STRETCH = List.of(95,96,97,98,99,100);
    private static final int[] HOME_STRETCH_ENTRIES = {50,11,24,37};


    public boolean hasValidMove(UUID gameId, UUID playerId, int diceValue) {
        //todo
        List<Token> playerTokens = tokenRepository.findByPlayerId(playerId);
        return playerTokens.stream().anyMatch(token -> token.getPosition() + diceValue <= 100);
    }

    private boolean handleTokenStart(Token token, int playerIndex, int diceValue) {
        if (token.getPosition() == -1 && diceValue == 6) {
            token.setPosition(STARTING_POSITIONS.get(playerIndex));
            return true;
        }
        return false;
    }


    public boolean moveToken(UUID gameId, UUID playerId, UUID tokenId, int diceValue) {
        Optional<Token> tokenOpt = tokenRepository.findById(tokenId);
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (tokenOpt.isPresent() && gameOpt.isPresent()) {
            Token token = tokenOpt.get();
            Game game = gameOpt.get();
            Player player = token.getPlayer();

            int playerIndex = game.getPlayers().indexOf(player); // p1 -0 , p2 - 1

            if (handleTokenStart(token, playerIndex, diceValue)) {
                tokenRepository.save(token);
                return true;
            }
            int newPosition = token.getPosition() + diceValue;
            // 1. if already in home stretch
            if (HOME_STRETCH.contains(token.getPosition())) {
                int currentIndex = HOME_STRETCH.indexOf(token.getPosition());
                int newIndex = currentIndex + diceValue;
                if (newIndex < HOME_STRETCH.size()) {
                    newPosition = HOME_STRETCH.get(newIndex);
                } else {
                    return false; // Move not possible
                }
            }
            // 2. when p1 token is at 51 and dice value moves it to >51
            else if (playerIndex == 0 && newPosition > 51) {
                int homeStretchIndex = newPosition - 51 - 1;
                if (homeStretchIndex < HOME_STRETCH.size()) {
                    newPosition = HOME_STRETCH.get(homeStretchIndex);
                } else {
                    return false; // Move not possible
                }
            }
            // 3. If Player 2,3,4 has to enter the home stretch
            else if (playerIndex > 0 && newPosition >= HOME_STRETCH_ENTRIES[playerIndex]) {
                int homeStretchIndex = newPosition - HOME_STRETCH_ENTRIES[playerIndex];
                if (homeStretchIndex < HOME_STRETCH.size()) {
                    newPosition = HOME_STRETCH.get(homeStretchIndex);
                } else {
                    return false; // Move not possible
                }
            }
            // 4. If Player 2,3,4 reach position 52+, loop back to 0.
            else if (playerIndex > 0 && newPosition > 51) {
                newPosition = newPosition - 52;
            }

          /*  if (token.getPosition() <= 51 && newPosition > 51) {
                newPosition = newPosition - 52;
            }
            if (token.getPosition() >= 0 && token.getPosition() <= 51) {
                int homeStretchEntry = HOME_STRETCH_ENTRIES[playerIndex];

                //homestretch entry
                if (newPosition >= homeStretchEntry) {
                    int homeStretchIndex = newPosition - homeStretchEntry;
                    if (homeStretchIndex < HOME_STRETCH.size()) {
                        newPosition = HOME_STRETCH.get(homeStretchIndex);
                    } else {
                        return false; // Move is not possible
                    }
                }
            }*/

            // cutting logic
            if (!HOME_STRETCH.contains(newPosition)) {
                List<Token> tokensAtNewPosition = tokenRepository.findByPosition(newPosition);
                for (Token otherToken : tokensAtNewPosition) {
                    if (!otherToken.getPlayer().getId().equals(player.getId()) && !SAFE_SPOTS.contains(newPosition)) {
                        otherToken.setPosition(-1);
                        tokenRepository.save(otherToken);
                    }
                }
            }

            token.setPosition(newPosition);
            tokenRepository.save(token);

            boolean hasWon = player.getTokens().stream().allMatch(t -> t.getPosition() == 100);
            if (hasWon) {
                game.setWinner(player);
                game.setGameOver(true);
                gameRepository.save(game);
            }

            webSocketService.notifyGameUpdate(gameId, game);

            return true;
        }
        return false;
    }

    }
