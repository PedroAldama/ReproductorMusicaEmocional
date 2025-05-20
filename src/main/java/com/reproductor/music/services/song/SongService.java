package com.reproductor.music.services.song;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.entities.Song;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public interface SongService {
     List<DTOSong> getAllSongsResponse();
     Song getSongByName(String name);
     DTOSong getSongByNameResponse(String name);
     DTOSong addSong(String song,double duration, MultipartFile file) throws IOException;
     void save(Song song);
     List<Song> findAllWithFeeling();
}
