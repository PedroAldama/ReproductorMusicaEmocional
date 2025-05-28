package com.reproductor.music.services.feelings;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.response.DTOSongFeelings;
import com.reproductor.music.dto.response.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;

import java.util.List;

public interface FeelingsService {
    void addFeelings(List<FeelingsRequest> feelings);
    void updateFeelings(FeelingsRequest feelings);
    List<DTOVectorSong> searchSongBySimilarFeelings(String song);
    DTOSongFeelings searchByName(String name);
    List<Double> getCurrentFeelingsByUser();
    List<DTOVectorSong> findMostSimilarSongs();
    List<Double> createCurrentFeeling();
    List<DTOSong> searchByUsername();
    DTOSong recommendationWebSocket();
    void setUser(String user);
}
