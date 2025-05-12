package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.DTOSongFeelings;
import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.SongFeelings;

import java.util.List;

public interface FeelingsService {
    void addFeelings(FeelingsRequest feelings);
    void updateFeelings(FeelingsRequest feelings);
    DTOVectorSong searchSongBySimilarFeelings(List<Integer> feelingValues, String name);

    DTOSongFeelings searchByName(String name);
}
