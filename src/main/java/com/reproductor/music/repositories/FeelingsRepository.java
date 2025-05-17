package com.reproductor.music.repositories;

import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FeelingsRepository extends CrudRepository<Feelings, Long> {
    Optional<Feelings> findById(long id);
}
