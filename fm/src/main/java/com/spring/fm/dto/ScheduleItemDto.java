package com.spring.fm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ScheduleItemDto {
    private UUID uuid;
    private String timestamp;
    private String section;
    private List<MealDto> meals;
    private List<WorkoutDto> workouts;
}
