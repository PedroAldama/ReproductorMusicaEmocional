package com.reproductor.music.repositories;

import com.reproductor.music.entities.user.ERole;
import com.reproductor.music.entities.user.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
