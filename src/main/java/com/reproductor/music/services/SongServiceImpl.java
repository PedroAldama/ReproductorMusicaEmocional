package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.entities.Song;
import com.reproductor.music.exceptions.SongException;
import com.reproductor.music.repositories.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import static com.reproductor.music.dto.Convert.convertSongList;
import static com.reproductor.music.dto.Convert.convertSongToDto;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Override
    public List<DTOSong> getAllSongsResponse() {
        return convertSongList(songRepository.findAll());
    }

    @Override
    public Song getSongByName(String name) {
        return songRepository.findByName(name)
                .orElseThrow(() -> new SongException.SongNotFoundException(name + " Not found"));
    }


    @Override
    public DTOSong getSongByNameResponse(String name) {
        return convertSongToDto(songRepository.findByName(name)
                .orElseThrow(() -> new SongException.SongNotFoundException(name + " Not found")));
    }

    @Override
    public DTOSong addSong(RequestSong song) {
        Song newSong = Song.builder()
                .name(song.getTitle())
                .duration(song.getDuration())
                .src(song.getSrc())
                .build();
        save(newSong);
        return getSongByNameResponse(newSong.getName());
    }

    @Override
    @Transactional
    public void save(Song song){
        songRepository.save(song);
    }


}
