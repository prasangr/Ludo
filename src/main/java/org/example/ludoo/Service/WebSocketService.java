package org.example.ludoo.Service;

import org.example.ludoo.Models.Game;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyGameUpdate(UUID gameId, Game game) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
    }
}