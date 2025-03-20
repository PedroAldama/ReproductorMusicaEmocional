package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.entities.Song;

import java.util.*;

public interface SongService {
     List<DTOSong> getAllSongsResponse();
     Song getSongByName(String name);
     DTOSong getSongByNameResponse(String name);
     DTOSong addSong(RequestSong song);
     void save(Song song);
}
