package com.spring.fm.ops.counters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MealCounterService {

    private final Counter mealAddCounter;
    private final Counter mealDeleteCounter;

    public MealCounterService(MeterRegistry meterRegistry) {
        this.mealAddCounter = meterRegistry.counter("service.meal.add");
        this.mealDeleteCounter = meterRegistry.counter("service.meal.delete");
    }

    public void countMealAdd() {
        this.mealAddCounter.increment();
    }

    public void countMealDelete() {
        this.mealDeleteCounter.increment();
    }
}
