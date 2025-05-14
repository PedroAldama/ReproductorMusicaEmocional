package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.DTOSongFeelings;
import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.entities.Users;

import java.util.List;

public interface FeelingsService {
    void addFeelings(FeelingsRequest feelings);
    void updateFeelings(FeelingsRequest feelings);
    DTOVectorSong searchSongBySimilarFeelings(String name);
    List<DTOVectorSong> getFeelingsByUser(String user, List<String> song);
    DTOSongFeelings searchByName(String name);
    List<Double> getCurrentFeelingsByUser(String user);
}
