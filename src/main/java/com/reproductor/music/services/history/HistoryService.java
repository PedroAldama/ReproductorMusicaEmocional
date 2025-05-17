package com.reproductor.music.services.history;

import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;

import java.util.Date;
import java.util.List;

public interface HistoryService {
    List<Song> recommendByEmotion(String user);
    void createHistory(String user);
    void addToHistory(String user,History history);
    History getHistory(String user, Date date);
    List<History> getAllHistory(String user);
    List<String> getSongsHistory(String user, Date date);
}
