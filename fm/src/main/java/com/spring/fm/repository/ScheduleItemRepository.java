package com.spring.fm.repository;

import com.spring.fm.model.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, UUID> {

    @Query("SELECT s FROM ScheduleItem s WHERE s.fmUserUuid = ?3 AND s.timestamp >= ?1 AND s.timestamp <= ?2")
    Optional<List<ScheduleItem>> customFindBetweenStartTimestampAndEndTimestampAndFmUserUuid(String startAt, String endAt, UUID userUuid);

    Optional<List<ScheduleItem>> findAllByMealUuidsAndFmUserUuid(UUID uuid, UUID userUuid);
    Optional<List<ScheduleItem>> findAllByWorkoutUuidsAndFmUserUuid(UUID uuid, UUID userUuid);
}
