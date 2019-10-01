package com.spring.fm.service.implementations;

import com.spring.fm.dto.MealDto;
import com.spring.fm.mapper.IDtoMapper;
import com.spring.fm.model.Meal;
import com.spring.fm.model.ScheduleItem;
import com.spring.fm.ops.counters.MealCounterService;
import com.spring.fm.ops.counters.ScheduleItemCounterService;
import com.spring.fm.repository.MealRepository;
import com.spring.fm.repository.ScheduleItemRepository;
import com.spring.fm.service.interfaces.IMealService;
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
public class MealService implements IMealService {

    private final MealRepository mealRepository;
    private final IDtoMapper iDtoMapper;
    private final ScheduleItemRepository scheduleItemRepository;
    private final MealCounterService mealCounterService;
    private final ScheduleItemCounterService scheduleItemCounterService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public MealService(MealRepository mealRepository,
                       IDtoMapper iDtoMapper,
                       ScheduleItemRepository scheduleItemRepository,
                       MealCounterService mealCounterService,
                       ScheduleItemCounterService scheduleItemCounterService) {
        this.mealRepository = mealRepository;
        this.iDtoMapper = iDtoMapper;
        this.scheduleItemRepository = scheduleItemRepository;
        this.mealCounterService = mealCounterService;
        this.scheduleItemCounterService = scheduleItemCounterService;
    }

    @Override
    @Transactional
    public List<MealDto> findAllByFmUserUuid(UUID uuid) {
        List<Meal> meals = this.mealRepository.findAllByFmUserUuid(uuid)
                .orElse(Collections.emptyList());
        return meals.stream()
                .peek(meal -> {
                    if (meal.getIngredients() == null) {
                        meal.setIngredients(Collections.emptyList());
                    }
                })
                .map(this.iDtoMapper::mealToMealDto)
                .collect(Collectors.toList());
    }

    @Override
    public MealDto addMeal(MealDto mealDto, UUID fmUserUuid) {
        try {

            Meal newMeal = this.iDtoMapper.mealDtoToMeal(mealDto);
            newMeal.setFmUserUuid(fmUserUuid);

            if (newMeal.getIngredients() == null || newMeal.getIngredients().size() == 0) {
                newMeal.setIngredients(null);
            } else {
                newMeal.setIngredients(newMeal.getIngredients().stream()
                        .filter(ingredient -> ingredient != null && ingredient.trim().length() != 0)
                        .collect(Collectors.toList()));
            }

            Meal saved = this.mealRepository.save(newMeal);
            this.mealCounterService.countMealAdd();
            return this.iDtoMapper
                    .mealToMealDto(saved);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteMeal(UUID uuid, UUID fmUserUuid) {
        this.cascadeDeleteMealInScheduleItem(uuid, fmUserUuid);
        try {
            Integer successfulDelete = this.mealRepository.deleteByUuidAndFmUserUuid(uuid, fmUserUuid);
            if (successfulDelete.equals(1)) {
                this.mealCounterService.countMealDelete();
            }
            return successfulDelete.equals(1);
        } catch (Exception e){
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MealDto updateMeal(MealDto mealDto, UUID userUuid) {
        Boolean exists = this.mealRepository.existsByUuidAndFmUserUuid(mealDto.getUuid(), userUuid);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Meal meal = this.iDtoMapper.mealDtoToMeal(mealDto);
        meal.setFmUserUuid(userUuid);

        if (meal.getIngredients() == null || meal.getIngredients().size() == 0) {
            meal.setIngredients(null);
        } else {
            meal.setIngredients(meal.getIngredients().stream()
                    .filter(ingredient -> ingredient != null && ingredient.trim().length() != 0)
                    .collect(Collectors.toList()));
        }

        try {
            return this.iDtoMapper
                    .mealToMealDto(this.mealRepository.save(meal));
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void cascadeDeleteMealInScheduleItem(UUID uuid, UUID userUuid) {
        try {
            List<ScheduleItem> scheduleItems =
                    this.scheduleItemRepository.findAllByMealUuidsAndFmUserUuid(uuid, userUuid)
                            .orElse(Collections.emptyList());

            if (scheduleItems.size() > 0) {
                List<ScheduleItem> scheduleItemsForDelete = new ArrayList<>();
                scheduleItems = scheduleItems.stream()
                        .peek(scheduleItem -> scheduleItem.setMealUuids(scheduleItem.getMealUuids().stream()
                                .filter(mealUuid -> !mealUuid.toString().equals(uuid.toString()))
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
