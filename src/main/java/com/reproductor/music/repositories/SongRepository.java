package com.reproductor.music.repositories;

import com.reproductor.music.entities.Album;
import com.reproductor.music.entities.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findByName(String name);
    List<Song> findByAlbum(Album album);
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.songFeelings sf LEFT JOIN FETCH sf.feeling")
    List<Song> findAllWithFeelings();
    @Query("select s.name from Song s")
    Set<String> findAllNames();
    @Query("select s.src from Song s")
    Set<String> findAllSrc();
    @Query("Select s.src from Song s where s.name = :songName")
    String findSrcByName(String songName);
    @Query("select s.name from Song s where s.name =:songName")
    String findOnlyName(String songName);
}
