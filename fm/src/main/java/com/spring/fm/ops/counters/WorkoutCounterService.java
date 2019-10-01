package com.spring.fm.ops.counters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class WorkoutCounterService {

    private final Counter workoutAddCounter;
    private final Counter workoutDeleteCounter;

    public WorkoutCounterService(MeterRegistry meterRegistry) {
        this.workoutAddCounter = meterRegistry.counter("service.workout.add");
        this.workoutDeleteCounter = meterRegistry.counter("service.workout.delete");
    }

    public void countWorkoutAdd() {
        this.workoutAddCounter.increment();
    }

    public void countWorkoutDelete() {
        this.workoutDeleteCounter.increment();
    }
}
