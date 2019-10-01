package com.spring.fm.service.implementations;

import com.spring.fm.dto.WorkoutDto;
import com.spring.fm.mapper.IDtoMapper;
import com.spring.fm.model.Endurance;
import com.spring.fm.model.ScheduleItem;
import com.spring.fm.model.Strength;
import com.spring.fm.model.Workout;
import com.spring.fm.ops.counters.ScheduleItemCounterService;
import com.spring.fm.ops.counters.WorkoutCounterService;
import com.spring.fm.repository.ScheduleItemRepository;
import com.spring.fm.repository.WorkoutRepository;
import com.spring.fm.service.interfaces.IWorkoutService;
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
public class WorkoutService implements IWorkoutService {

    private final WorkoutRepository workoutRepository;
    private final IDtoMapper iDtoMapper;
    private final ScheduleItemRepository scheduleItemRepository;
    private final WorkoutCounterService workoutCounterService;
    private final ScheduleItemCounterService scheduleItemCounterService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository,
                          IDtoMapper iDtoMapper,
                          ScheduleItemRepository scheduleItemRepository,
                          WorkoutCounterService workoutCounterService,
                          ScheduleItemCounterService scheduleItemCounterService) {
        this.workoutRepository = workoutRepository;
        this.iDtoMapper = iDtoMapper;
        this.scheduleItemRepository = scheduleItemRepository;
        this.workoutCounterService = workoutCounterService;
        this.scheduleItemCounterService = scheduleItemCounterService;
    }

    @Override
    public List<WorkoutDto> findAllByFmUserUuid(UUID userUuid) {
        try {
            return this.workoutRepository.findAllByFmUserUuid(userUuid)
                    .orElse(Collections.emptyList()).stream()
                    .peek(workout -> {
                        if (workout.getType().equals("strength")) {
                            workout.setEndurance(Endurance.emptyEndurance());
                        } else if (workout.getType().equals("endurance")) {
                            workout.setStrength(Strength.emptyStrength());
                        }
                    })
                    .map(this.iDtoMapper::workoutToWorkoutDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public WorkoutDto addWorkout(WorkoutDto workoutDto, UUID userUuid) {
        try {
            Workout newWorkout = this.iDtoMapper.workoutDtoToWorkout(workoutDto);
            newWorkout.setFmUserUuid(userUuid);

            Workout saved = this.workoutRepository.save(newWorkout);
            this.workoutCounterService.countWorkoutAdd();
            return this.iDtoMapper
                    .workoutToWorkoutDto(saved);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteWorkout(UUID uuid, UUID userUuid) {
        this.cascadeDeleteWorkoutInScheduleItem(uuid, userUuid);
        try {
            Integer successfulDelete = this.workoutRepository.deleteByUuidAndFmUserUuid(uuid, userUuid);
            if (successfulDelete.equals(1)) {
                this.workoutCounterService.countWorkoutDelete();
            }
            return successfulDelete.equals(1);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public WorkoutDto updateWorkout(WorkoutDto workoutDto, UUID userUuid) {
        Boolean exists = this.workoutRepository.existsByUuidAndFmUserUuid(workoutDto.getUuid(), userUuid);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Workout workout = this.iDtoMapper.workoutDtoToWorkout(workoutDto);
        workout.setFmUserUuid(userUuid);

        try {
            return this.iDtoMapper
                    .workoutToWorkoutDto(this.workoutRepository
                            .save(workout));
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void cascadeDeleteWorkoutInScheduleItem(UUID uuid, UUID userUuid) {
        try {
            List<ScheduleItem> scheduleItems =
                    this.scheduleItemRepository.findAllByWorkoutUuidsAndFmUserUuid(uuid, userUuid)
                            .orElse(Collections.emptyList());

            if (scheduleItems.size() > 0) {
                List<ScheduleItem> scheduleItemsForDelete = new ArrayList<>();
                scheduleItems = scheduleItems.stream()
                        .peek(scheduleItem -> scheduleItem.setWorkoutUuids(scheduleItem.getWorkoutUuids().stream()
                                .filter(workoutUuid -> !workoutUuid.toString().equals(uuid.toString()))
                                .collect(Collectors.toSet())))
                        .peek(scheduleItem -> {
                            if ((scheduleItem.getMealUuids() == null || scheduleItem.getMealUuids().size() == 0) &&
                                    (scheduleItem.getWorkoutUuids() == null || scheduleItem.getWorkoutUuids().size() == 0)) {
                                scheduleItemsForDelete.add(scheduleItem);
                            }
                        })
                        .collect(Collectors.toList());
                this.scheduleItemRepository.saveAll(scheduleItems);
                if (scheduleItemsForDelete.size() > 0) {
                    this.scheduleItemRepository.deleteAll(scheduleItemsForDelete);
                    this.scheduleItemCounterService.countParamScheduleItemDelete(scheduleItemsForDelete.size());
                }
            }
        } catch (Exception e) {
            logger.error(Arrays.asList(e.getStackTrace()).toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
