package com.reproductor.music.repositories;

import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongFeelingRepository extends CrudRepository<SongFeelings,Long> {

    @Query("SELECT sf.song from SongFeelings sf where sf.user.username = :user group by sf.song")
    List<Song> getSongsByUser(@Param("user") String user);

    @Query("SELECT sf.song.name from SongFeelings sf where sf.user.username = :user group by sf.song.name")
    List<String> getSongsNameByUser(@Param("user") String user);

    @Query("Select sf from SongFeelings sf Where sf.user.username = :user and sf.song.name = :song")
    List<SongFeelings> findByUser_UsernameAndSong_Name(@Param("song") String song,@Param("user") String user);

    @Query("Select sf.value from SongFeelings sf where sf.user.username = :user and sf.song.name = :song")
    List<Double> findUserSongValues(@Param("user") String user,@Param("song") String song);

    @Query("Select count(distinct(sf.song)) from SongFeelings sf where sf.user.username = :user")
    Integer countSongByUser(@Param("user") String user);
}
