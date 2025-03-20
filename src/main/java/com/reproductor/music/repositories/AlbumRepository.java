package com.reproductor.music.repositories;

import com.reproductor.music.entities.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByTitle(String name);
    List<Album> findByGroup_Name(String artist);
    List<Album> findByReleaseDateIsBetween(int start, int end);
    List<Album> findByGroup_NameAndReleaseDateIsBetween(String artist, int start, int end);
}
