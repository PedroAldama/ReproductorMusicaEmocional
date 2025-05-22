package com.reproductor.music.services.spotify;

import com.reproductor.music.dto.response.DTOSong;

import java.util.List;

public interface SpotifyService {
    String getAccessToken();
    List<DTOSong> searchAndSaveSongs(String query);

}
