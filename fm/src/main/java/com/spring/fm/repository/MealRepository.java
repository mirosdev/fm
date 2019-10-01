package com.spring.fm.repository;

import com.spring.fm.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {
    Optional<List<Meal>> findAllByFmUserUuid(UUID uuid);
    Integer deleteByUuidAndFmUserUuid(UUID uuid, UUID userUuid);
    Boolean existsByUuidAndFmUserUuid(UUID uuid, UUID userUuid);
    Optional<List<Meal>> findAllByUuidInAndFmUserUuid(Set<UUID> uuids, UUID userUuid);
}
