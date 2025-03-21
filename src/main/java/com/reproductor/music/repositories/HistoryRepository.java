package com.reproductor.music.repositories;

import com.reproductor.music.entities.History;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoryRepository extends MongoRepository<History, String> {
    List<History> findByUserOrderByCreationDesc(String user);
}
