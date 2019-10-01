package com.spring.fm.service.implementations;

import com.spring.fm.dto.ScheduleItemDto;
import com.spring.fm.dto.ScheduleItemPayload;
import com.spring.fm.mapper.IDtoMapper;
import com.spring.fm.model.Meal;
import com.spring.fm.model.ScheduleItem;
import com.spring.fm.model.Workout;
import com.spring.fm.ops.counters.ScheduleItemCounterService;
import com.spring.fm.repository.MealRepository;
import com.spring.fm.repository.ScheduleItemRepository;
import com.spring.fm.repository.WorkoutRepository;
import com.spring.fm.service.interfaces.IScheduleItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleItemService implements IScheduleItemService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final MealRepository mealRepository;
    private final WorkoutRepository workoutRepository;
    private final IDtoMapper iDtoMapper;
    private final ScheduleItemCounterService scheduleItemCounterService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ScheduleItemService(ScheduleItemRepository scheduleItemRepository,
                               MealRepository mealRepository,
                               WorkoutRepository workoutRepository,
                               IDtoMapper iDtoMapper,
                               ScheduleItemCounterService scheduleItemCounterService) {
        this.scheduleItemRepository = scheduleItemRepository;
        this.mealRepository = mealRepository;
        this.workoutRepository = workoutRepository;
        this.iDtoMapper = iDtoMapper;
        this.scheduleItemCounterService = scheduleItemCounterService;
    }

    @Override
    @Transactional
    public ScheduleItemDto create(ScheduleItemPayload scheduleItemPayload, UUID userUuid) {
        try {
            ScheduleItem scheduleItem = this.iDtoMapper.scheduleItemPayloadToScheduleItem(scheduleItemPayload);
            scheduleItem.setFmUserUuid(userUuid);

            ScheduleItemDto scheduleItemDto = this.iDtoMapper
                    .scheduleItemToScheduleItemDto(this.scheduleItemRepository.save(scheduleItem));

            if (scheduleItem.getMealUuids() != null && scheduleItem.getMealUuids().size() > 0) {
                scheduleItemDto.setMeals(this.mealRepository.findAllByUuidInAndFmUserUuid(scheduleItem.getMealUuids(), userUuid)
                        .orElse(Collections.emptyList()).stream()
                        .peek(meal -> {
                            if (meal.getIngredients() == null || meal.getIngredients().size() == 0) {
                                meal.setIngredients(Collections.emptyList());
                            }
                        })
                        .map(this.iDtoMapper::mealToMealDto).collect(Collectors.toList()));
            } else {
                scheduleItemDto.setMeals(Collections.emptyList());
            }

            if (scheduleItem.getWorkoutUuids() != null && scheduleItem.getWorkoutUuids().size() > 0) {
                scheduleItemDto.setWorkouts(this.workoutRepository.findAllByUuidInAndFmUserUuid(scheduleItem.getWorkoutUuids(), userUuid)
                        .orElse(Collections.emptyList()).stream()
                        .map(this.iDtoMapper::workoutToWorkoutDto).collect(Collectors.toList()));
            } else {
                scheduleItemDto.setWorkouts(Collections.emptyList());
            }
            this.scheduleItemCounterService.countScheduleItemAdd();
            return scheduleItemDto;
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ScheduleItemDto update(ScheduleItemPayload scheduleItemPayload, UUID uuid, UUID userUuid) {
        ScheduleItem scheduleItem = this.scheduleItemRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        if (scheduleItem.getFmUserUuid().toString().equals(userUuid.toString())) {
            if ((scheduleItemPayload.getMeals() == null || scheduleItemPayload.getMeals().size() == 0) &&
                    (scheduleItemPayload.getWorkouts() == null || scheduleItemPayload.getWorkouts().size() == 0)) {
                this.scheduleItemRepository.deleteById(uuid);
                return null;
            } else {
                scheduleItem.setSection(scheduleItemPayload.getSection());
                scheduleItem.setTimestamp(scheduleItemPayload.getTimestamp());
                if (scheduleItemPayload.getMeals() != null && scheduleItemPayload.getMeals().size() > 0) {
                    scheduleItem.setMealUuids(new HashSet<>(scheduleItemPayload.getMeals()));
                } else {
                    scheduleItem.setMealUuids(null);
                }
                if (scheduleItemPayload.getWorkouts() != null && scheduleItemPayload.getWorkouts().size() > 0) {
                    scheduleItem.setWorkoutUuids(new HashSet<>(scheduleItemPayload.getWorkouts()));
                } else {
                    scheduleItem.setWorkoutUuids(null);
                }
                try {
                    ScheduleItemDto scheduleItemDto = this.iDtoMapper
                            .scheduleItemToScheduleItemDto(this.scheduleItemRepository.save(scheduleItem));

                    if (scheduleItem.getMealUuids() != null && scheduleItem.getMealUuids().size() > 0) {
                        scheduleItemDto.setMeals(this.mealRepository.findAllByUuidInAndFmUserUuid(scheduleItem.getMealUuids(), userUuid)
                                .orElse(Collections.emptyList()).stream()
                                .peek(meal -> {
                                    if (meal.getIngredients() == null || meal.getIngredients().size() == 0) {
                                        meal.setIngredients(Collections.emptyList());
                                    }
                                })
                                .map(this.iDtoMapper::mealToMealDto).collect(Collectors.toList()));
                    } else {
                        scheduleItemDto.setMeals(Collections.emptyList());
                    }

                    if (scheduleItem.getWorkoutUuids() != null && scheduleItem.getWorkoutUuids().size() > 0) {
                        scheduleItemDto.setWorkouts(this.workoutRepository.findAllByUuidInAndFmUserUuid(scheduleItem.getWorkoutUuids(), userUuid)
                                .orElse(Collections.emptyList()).stream()
                                .map(this.iDtoMapper::workoutToWorkoutDto).collect(Collectors.toList()));
                    } else {
                        scheduleItemDto.setWorkouts(Collections.emptyList());
                    }
                    return scheduleItemDto;
                } catch (Exception e) {
                    logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public List<ScheduleItemDto> findBetweenTimestamps(String startAt, String endAt, UUID userUuid) {

        List<ScheduleItem> scheduleItems = this.scheduleItemRepository.customFindBetweenStartTimestampAndEndTimestampAndFmUserUuid(startAt, endAt, userUuid)
                .orElse(Collections.emptyList());

        List<ScheduleItemDto> scheduleItemDtos = scheduleItems.stream()
                .map(this.iDtoMapper::scheduleItemToScheduleItemDto)
                .collect(Collectors.toList());

        Set<UUID> mealUuids = new HashSet<>();
        Set<UUID> workoutUuids = new HashSet<>();

        if (scheduleItems.size() > 0) {
            for (ScheduleItem scheduleItem : scheduleItems) {
                if (scheduleItem.getMealUuids() != null && scheduleItem.getMealUuids().size() > 0) {
                    mealUuids.addAll(scheduleItem.getMealUuids());

                }
                if (scheduleItem.getWorkoutUuids() != null && scheduleItem.getWorkoutUuids().size() > 0) {
                    workoutUuids.addAll(scheduleItem.getWorkoutUuids());
                }
            }

            if (mealUuids.size() > 0) {
                List<Meal> meals = this.mealRepository.findAllByUuidInAndFmUserUuid(mealUuids, userUuid)
                        .orElse(Collections.emptyList()).stream()
                        .peek(meal -> {
                            if (meal.getIngredients() == null || meal.getIngredients().size() == 0) {
                                meal.setIngredients(Collections.emptyList());
                            }
                        }).collect(Collectors.toList());

                if (meals.size() > 0) {
                    for (ScheduleItem scheduleItem : scheduleItems) {
                        List<Meal> scheduleItemMeals = new ArrayList<>();
                        for (Meal meal : meals) {
                            if (scheduleItem.getMealUuids() != null && scheduleItem.getMealUuids().size() > 0) {
                                for (UUID mealUuid : scheduleItem.getMealUuids()) {
                                    if (mealUuid.toString().equals(meal.getUuid().toString())) {
                                        scheduleItemMeals.add(meal);
                                    }
                                }
                            }
                        }
                        if (scheduleItemMeals.size() > 0) {
                            for (ScheduleItemDto scheduleItemDto : scheduleItemDtos) {
                                if (scheduleItemDto.getUuid().toString().equals(scheduleItem.getUuid().toString())) {
                                    scheduleItemDto.setMeals(scheduleItemMeals.stream()
                                            .map(this.iDtoMapper::mealToMealDto)
                                            .collect(Collectors.toList()));
                                }
                            }
                        }
                    }
                }
            }
            if (workoutUuids.size() > 0) {
                List<Workout> workouts = this.workoutRepository.findAllByUuidInAndFmUserUuid(workoutUuids, userUuid)
                        .orElse(Collections.emptyList());

                if (workouts.size() > 0) {
                    for (ScheduleItem scheduleItem : scheduleItems) {
                        List<Workout> scheduleItemWorkouts = new ArrayList<>();
                        for (Workout workout : workouts) {
                            if (scheduleItem.getWorkoutUuids() != null && scheduleItem.getWorkoutUuids().size() > 0) {
                                for (UUID workoutUuid : scheduleItem.getWorkoutUuids()) {
                                    if (workoutUuid.toString().equals(workout.getUuid().toString())) {
                                        scheduleItemWorkouts.add(workout);
                                    }
                                }
                            }
                        }
                        if (scheduleItemWorkouts.size() > 0) {
                            for (ScheduleItemDto scheduleItemDto : scheduleItemDtos) {
                                if (scheduleItemDto.getUuid().toString().equals(scheduleItem.getUuid().toString())) {
                                    scheduleItemDto.setWorkouts(scheduleItemWorkouts.stream()
                                            .map(this.iDtoMapper::workoutToWorkoutDto)
                                            .collect(Collectors.toList()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return scheduleItemDtos;
    }
}
