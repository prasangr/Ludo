/*
package org.example.ludoo.Service;

import org.example.ludoo.Models.Game;
import org.example.ludoo.Models.Player;
import org.example.ludoo.Repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService gameService;
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository = mock(GameRepository.class);
        gameService = new GameService(gameRepository);
    }

    @Test
    void testIsPlayerTurn_True() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);

        Game game = new Game();
        game.setId(gameId);
        game.setCurrentPlayer(player);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertTrue(gameService.isPlayerTurn(gameId, playerId));
    }

    @Test
    void testIsPlayerTurn_False() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        UUID otherPlayerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(otherPlayerId);

        Game game = new Game();
        game.setId(gameId);
        game.setCurrentPlayer(player);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertFalse(gameService.isPlayerTurn(gameId, playerId));
    }

    @Test
    void testNextTurn_SwitchesToNextPlayer() {
        UUID gameId = UUID.randomUUID();
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();

        Player player1 = new Player();
        player1.setId(player1Id);

        Player player2 = new Player();
        player2.setId(player2Id);

        Game game = new Game();
        game.setId(gameId);
        game.setPlayers(List.of(player1, player2));
        game.setCurrentPlayer(player1);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        gameService.nextTurn(gameId);

        assertEquals(player2, game.getCurrentPlayer());
        verify(gameRepository, times(1)).save(game);
    }
}*/
