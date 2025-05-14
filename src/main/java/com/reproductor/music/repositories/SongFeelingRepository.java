package com.reproductor.music.repositories;

import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.entities.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongFeelingRepository extends CrudRepository<SongFeelings,Long> {
    List<SongFeelings> findBySong(Song song);
    List<SongFeelings> findBySongAndUser(Song song, Users user);
}
