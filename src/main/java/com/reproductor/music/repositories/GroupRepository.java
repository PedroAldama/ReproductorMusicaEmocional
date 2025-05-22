package com.reproductor.music.repositories;

import com.reproductor.music.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);

    @Query("Select g.name from Group g")
    Set<String> findAllNames();
}
