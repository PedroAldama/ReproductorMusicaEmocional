package com.reproductor.music.services;

import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;

import java.util.Date;
import java.util.List;

public interface HistoryService {
    List<Song> recommendByEmotion(String user);
    void createHistory(String user);
    void addToHistory(String user, Song song);
    History getHistory(String user, Date date);
    List<History> getAllHistory();
}
