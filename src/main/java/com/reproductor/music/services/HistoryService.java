package com.reproductor.music.services;

import com.reproductor.music.entities.Song;

import java.util.List;

public interface HistoryService {
    List<Song> recommendByEmotion(String user);

}
