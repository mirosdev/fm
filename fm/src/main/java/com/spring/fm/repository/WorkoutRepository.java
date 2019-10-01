package com.spring.fm.repository;

import com.spring.fm.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
    Optional<List<Workout>> findAllByFmUserUuid(UUID userUuid);
    Integer deleteByUuidAndFmUserUuid(UUID uuid, UUID userUuid);
    Optional<List<Workout>> findAllByUuidInAndFmUserUuid(Set<UUID> uuids, UUID userUuid);
    Boolean existsByUuidAndFmUserUuid(UUID uuid, UUID userUuid);
}
