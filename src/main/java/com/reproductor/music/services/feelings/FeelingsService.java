package com.reproductor.music.services.feelings;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.DTOSongFeelings;
import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;

import java.util.List;

public interface FeelingsService {
    void addFeelings(FeelingsRequest feelings,String user);
    void updateFeelings(FeelingsRequest feelings, String user);
    List<DTOVectorSong> searchSongBySimilarFeelings(String song,String name);
    DTOSongFeelings searchByName(String name, String user);
    List<Double> getCurrentFeelingsByUser(String user);
    List<DTOVectorSong> findMostSimilarSongs(String user);
    List<Double> createCurrentFeeling(String user);
    List<DTOSong> searchByUsername(String name);
}
