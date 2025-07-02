package com.reproductor.music.services.cache;

import java.util.List;

public interface FeelingSongCacheService {
    List<Double> getCurrentFeeling(String username);
    void setCurrentFeeling(String username, List<Double> feelings);
    void addSongToHistory(String username, String song);
    List<String> getSongHistory(String username);
    List<Double> getUserSongVector(String username, String songName);
    void saveUserSongVector(String username, String songName, List<Double> vector);
    List<String> getUserCachedSongs(String username);
    void cacheUserSong(String username, String songName);
    List<String> getFullUserSongList(String username);
}
