package com.reproductor.music.repositories;

import com.reproductor.music.entities.History;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends MongoRepository<History, String> {
    List<History> findByUserOrderByCreationDesc(String user);
    Optional<History> findById(String id);
    List<History> findAll();
}
