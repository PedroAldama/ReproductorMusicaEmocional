package com.reproductor.music.repositories;

import com.reproductor.music.entities.ERole;
import com.reproductor.music.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
