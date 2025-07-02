package com.reproductor.music.services.feelings;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.response.DTOSongFeelings;
import com.reproductor.music.dto.response.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;

import java.util.List;

public interface FeelingsService {
    void addFeelings(List<FeelingsRequest> feelings);
    List<DTOVectorSong> searchSongBySimilarFeelings(String song);
    DTOSongFeelings searchByName(String name);
    List<Double> getCurrentFeelingsByUser(String userName);
    List<DTOVectorSong> findMostSimilarSongs(String userName);
    List<Double> createCurrentFeeling(String username);
    List<DTOSong> searchByUsername();
}
