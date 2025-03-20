package com.reproductor.music.repositories;

import com.reproductor.music.entities.Album;
import com.reproductor.music.entities.Group;
import com.reproductor.music.entities.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findByName(String name);
    List<Song> findByAlbum(Album album);
}
