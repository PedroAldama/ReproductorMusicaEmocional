package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.DTOSongFeelings;
import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.entities.Users;

import java.util.List;

public interface FeelingsService {
    void addFeelings(FeelingsRequest feelings,String user);
    void updateFeelings(FeelingsRequest feelings, String user);
    DTOVectorSong searchSongBySimilarFeelings(String name);
    List<DTOVectorSong> getFeelingsByUser(String user, List<Song> song);
    DTOSongFeelings searchByName(String name, String user);
    List<Double> getCurrentFeelingsByUser(String user);
    List<DTOVectorSong> findMostSimilarSongs(String user);
}
