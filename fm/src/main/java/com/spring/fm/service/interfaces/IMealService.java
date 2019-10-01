package com.spring.fm.service.interfaces;

import com.spring.fm.dto.MealDto;

import java.util.List;
import java.util.UUID;

public interface IMealService {
    List<MealDto> findAllByFmUserUuid(UUID uuid);
    MealDto addMeal(MealDto mealDto, UUID fmUserUuid);
    Boolean deleteMeal(UUID uuid, UUID fmUserUuid);
    MealDto updateMeal(MealDto mealDto, UUID userUuid);
}
