package com.reproductor.music.services.spotify;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.repositories.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {
    @Value("${spotify.client-id}")
    private String clientId;
    @Value("${spotify.client-secret}")
    private String clientSecret;

    private final WebClient webClient;
    private final SongRepository songRepository;
    private final MusicServiceImpl musicService;

    @Override
    public String getAccessToken() {
        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> (String) body.get("access_token"))
                .block();
    }

    @Override
    public List<DTOSong> searchAndSaveSongs(String playlistId) {
        String accessToken = getAccessToken();
        String url = "https://api.spotify.com/v1/playlists/"
                + playlistId
                + "/tracks?fields=items(track(name,uri,duration_ms,album(name,release_date),artists(name)))"
                + "&limit=100&offset=250";

        Map response = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

         return musicService.saveSongs(items);
    }
}
