package com.reproductor.music.repositories;

import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeelingsRepository extends CrudRepository<Feelings, Long> {
}
