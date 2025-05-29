package com.reproductor.music.controllers;

import com.reproductor.music.dto.request.DTOInteraction;
import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.services.feelings.websocket.WebSocketFeelingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketFeelingsService webSocketFeelingsService;

    @MessageMapping("/recommend/interaction")
    public void handleInteraction(@Payload DTOInteraction interaction, Principal principal) {
        String username = principal.getName();
        DTOSong nextSong = webSocketFeelingsService.recommendationWebSocket(username);

        messagingTemplate.convertAndSendToUser(username,
                "/queue/recommendation",
                nextSong);
    }

}
