package com.reproductor.music.repositories;

import com.reproductor.music.entities.Lista;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ListRepository extends MongoRepository<Lista, String> {
    Optional<Lista> findByName(String name);
    List<Lista> findByCreationBetween(Date start, Date end);
}
