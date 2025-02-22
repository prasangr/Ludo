package org.example.ludoo.Service;

import org.example.ludoo.Models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.UUID;
import static org.mockito.Mockito.*;

class WebSocketServiceTest {

    private WebSocketService webSocketService;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        webSocketService = new WebSocketService(messagingTemplate);
    }

    @Test
    void testNotifyGameUpdate() {
        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.setId(gameId);

        webSocketService.notifyGameUpdate(gameId, game);

        verify(messagingTemplate, times(1)).convertAndSend("/topic/game/" + gameId, game);
    }
}