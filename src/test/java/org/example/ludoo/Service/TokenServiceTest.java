package org.example.ludoo.Service;

import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Models.Token;
import org.example.ludoo.Repository.GameRepository;
import org.example.ludoo.Repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

        Token token = new Token();
        token.setId(tokenId);
        token.setPosition(5);
        token.setPlayer(player);

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 3);

        assertTrue(result);
        assertEquals(8, token.getPosition());
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void testMoveToken_TokenDoesNotMoveIfNotSix() {
        UUID tokenId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);

        Token token = new Token();
        token.setId(tokenId);
        token.setPosition(0);
        token.setPlayer(player);

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 5);

        assertFalse(result);
        assertEquals(0, token.getPosition());
    }

    @Test
    void testMoveToken_PlayerWins() {
        UUID tokenId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);

        Token token1 = new Token();
        token1.setPosition(57);

        Token token2 = new Token();
        token2.setPosition(57);

        Token token3 = new Token();
        token3.setPosition(57);

        Token token4 = new Token();
        token4.setPosition(56);
        token4.setId(tokenId);
        token4.setPlayer(player);

        Game game = new Game();
        game.setId(gameId);
        game.setPlayers(List.of(player));

        when(tokenRepository.findById(tokenId)).thenReturn(Optional.of(token4));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(tokenRepository.findByPlayerId(playerId)).thenReturn(List.of(token1, token2, token3, token4));

        boolean result = tokenService.moveToken(gameId, playerId, tokenId, 1);

        assertTrue(result);
        assertTrue(game.isGameOver());
        assertEquals(player, game.getWinner());
    }
}