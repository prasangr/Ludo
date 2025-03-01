// TokenServiceTest.java
package org.example.ludoo.Service;

import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Models.Token;
import org.example.ludoo.Repository.GameRepository;
import org.example.ludoo.Repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    private TokenService tokenService;
    private TokenRepository tokenRepository;
    private GameRepository gameRepository;
    private WebSocketService webSocketService;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(TokenRepository.class);
        gameRepository = mock(GameRepository.class);
        webSocketService = mock(WebSocketService.class);
        gameService = mock(GameService.class);
        tokenService = new TokenService(tokenRepository, gameRepository, webSocketService, gameService);
    }

    @Test
    void testMoveToken_SuccessfulMove() {
        UUID tokenId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);

        Token token = new Token(player);
        token.setId(tokenId);
        token.setPosition(5);
        token.setPlayer(player);

        player.setTokens(List.of(token)); // Add token to player's tokens list

        Game game = new Game();
        game.setId(gameId);
        game.setPlayers(List.of(player));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 3);

        assertTrue(result);
        assertEquals(8, token.getPosition());
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void testMoveToken_GameNotFound() {
        UUID tokenId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Token token = new Token();
        token.setId(tokenId);

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 3);

        assertFalse(result);
    }

    @Test
    void testMoveToken_MoveToHomeStretch() {
        UUID tokenId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);

        Token token = new Token(player);
        token.setId(tokenId);
        token.setPosition(50);
        token.setPlayer(player);

        player.setTokens(List.of(token));

        Game game = new Game();
        game.setId(gameId);
        game.setPlayers(List.of(player));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 3);
        System.out.println(token.getPosition());
        assertTrue(result);
        assertEquals(96, token.getPosition());
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void testMoveToken_PlayerWins() {
        UUID tokenId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);
        Token token1 = new Token(player);
        token1.setPosition(100);
        Token token2 = new Token(player);
        token2.setPosition(100);
        Token token3 = new Token(player);
        token3.setPosition(100);
        Token token4 = new Token(player);
        token4.setPosition(99);
        token4.setId(tokenId);
        token4.setPlayer(player);

        player.setTokens(List.of(token1, token2, token3, token4));

        Game game = new Game();
        game.setId(gameId);
        game.setPlayers(List.of(player));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token4));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(tokenRepository.findByPlayerId(playerId)).thenReturn(List.of(token1, token2, token3, token4));

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 1);

        assertTrue(result);
        assertTrue(game.isGameOver());
    //    System.out.println(game.getWinner());
        assertEquals(player, game.getWinner());
    }
}