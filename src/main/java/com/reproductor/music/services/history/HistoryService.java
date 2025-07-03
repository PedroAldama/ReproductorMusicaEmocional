package com.reproductor.music.services.history;

import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;

import java.util.Date;
import java.util.List;

public interface HistoryService {
    void createHistory(String user);
    History getHistory(Date date,String user);
    List<History> getAllHistory();
}