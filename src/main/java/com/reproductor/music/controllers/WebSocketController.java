package com.reproductor.music.controllers;

import com.reproductor.music.dto.request.DTOInteraction;
import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.services.feelings.FeelingsService;
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
    private final FeelingsService feelingsService;

    @MessageMapping("/recommend/interaction")
    public void handleInteraction(@Payload DTOInteraction interaction, Principal principal) {
        String username = principal.getName();
        feelingsService.setUser(username);
        DTOSong nextSong = feelingsService.recommendationWebSocket();

        messagingTemplate.convertAndSendToUser(username,
                "/queue/recommendation",
                nextSong);
    }

}
