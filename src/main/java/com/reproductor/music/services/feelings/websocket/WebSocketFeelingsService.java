package com.reproductor.music.services.feelings.websocket;

import com.reproductor.music.dto.response.DTOSong;

public interface WebSocketFeelingsService {
    public DTOSong recommendationWebSocket(String userName);
}
