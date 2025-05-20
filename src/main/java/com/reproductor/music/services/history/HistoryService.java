package com.reproductor.music.services.history;

import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;

import java.util.Date;
import java.util.List;

public interface HistoryService {
    List<Song> recommendByEmotion();
    void createHistory(String user);
    void addToHistory(History history);
    History getHistory(Date date);
    List<History> getAllHistory();
    List<String> getSongsHistory(Date date);
}
