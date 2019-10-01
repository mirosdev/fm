package com.spring.fm.mapper;

import com.spring.fm.dto.*;
import com.spring.fm.model.*;
import com.spring.fm.security.UserRegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface IDtoMapper {
    @Mappings({
            @Mapping(target="uuid", source="fmUser.uuid"),
            @Mapping(target="email", source="fmUser.email")
    })
    FmUserDto fmUserToFmUserDto(FmUser fmUser);

    @Mappings({
            @Mapping(target="email", source="userRegistrationDto.email"),
            @Mapping(target="password", source="userRegistrationDto.password")
    })
    FmUser userRegistrationDtoToFmUser(UserRegistrationDto userRegistrationDto);

    @Mappings({
            @Mapping(target = "uuid", source = "meal.uuid"),
            @Mapping(target = "name", source = "meal.name"),
            @Mapping(target = "ingredients", source = "meal.ingredients")
    })
    MealDto mealToMealDto(Meal meal);

    @Mappings({
            @Mapping(target = "uuid", source = "mealDto.uuid"),
            @Mapping(target = "name", source = "mealDto.name"),
            @Mapping(target = "ingredients", source = "mealDto.ingredients")
    })
    Meal mealDtoToMeal(MealDto mealDto);

    @Mappings({
            @Mapping(target = "uuid", source = "workout.uuid"),
            @Mapping(target = "name", source = "workout.name"),
            @Mapping(target = "type", source = "workout.type"),
            @Mapping(target = "strength", source = "workout.strength"),
            @Mapping(target = "endurance", source = "workout.endurance")
    })
    WorkoutDto workoutToWorkoutDto(Workout workout);

    @Mappings({
            @Mapping(target = "uuid", source = "workoutDto.uuid"),
            @Mapping(target = "name", source = "workoutDto.name"),
            @Mapping(target = "type", source = "workoutDto.type"),
            @Mapping(target = "strength", source = "workoutDto.strength"),
            @Mapping(target = "endurance", source = "workoutDto.endurance")
    })
    Workout workoutDtoToWorkout(WorkoutDto workoutDto);

    //remaining fields are mapped with setters
    @Mappings({
            @Mapping(target = "uuid", source = "scheduleItem.uuid"),
            @Mapping(target = "timestamp", source = "scheduleItem.timestamp"),
            @Mapping(target = "section", source = "scheduleItem.section")
    })
    ScheduleItemDto scheduleItemToScheduleItemDto(ScheduleItem scheduleItem);

    // uuid may be mapped with setter currently
    @Mappings({
            @Mapping(target = "section", source = "scheduleItemPayload.section"),
            @Mapping(target = "timestamp", source = "scheduleItemPayload.timestamp"),
            @Mapping(target = "mealUuids", source = "scheduleItemPayload.meals"),
            @Mapping(target = "workoutUuids", source = "scheduleItemPayload.workouts")
    })
    ScheduleItem scheduleItemPayloadToScheduleItem(ScheduleItemPayload scheduleItemPayload);
}
