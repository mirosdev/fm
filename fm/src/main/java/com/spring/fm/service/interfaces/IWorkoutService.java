package com.spring.fm.service.interfaces;

import com.spring.fm.dto.WorkoutDto;

import java.util.List;
import java.util.UUID;

public interface IWorkoutService {
    List<WorkoutDto> findAllByFmUserUuid(UUID userUuid);
    WorkoutDto addWorkout(WorkoutDto workoutDto, UUID userUuid);
    Boolean deleteWorkout(UUID uuid, UUID userUuid);
    WorkoutDto updateWorkout(WorkoutDto workoutDto, UUID userUuid);
}
