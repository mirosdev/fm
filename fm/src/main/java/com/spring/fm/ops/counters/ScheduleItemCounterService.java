package com.spring.fm.ops.counters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ScheduleItemCounterService {

    private final Counter scheduleItemAddCounter;
    private final Counter scheduleItemDeleteCounter;


    public ScheduleItemCounterService(MeterRegistry meterRegistry) {
        this.scheduleItemAddCounter = meterRegistry.counter("service.schedule-item.add");
        this.scheduleItemDeleteCounter = meterRegistry.counter("service.schedule-item.delete");
    }

    public void countScheduleItemAdd() {
        this.scheduleItemAddCounter.increment();
    }

    public void countParamScheduleItemDelete(double incrementValue) {
        this.scheduleItemDeleteCounter.increment(incrementValue);
    }
}
