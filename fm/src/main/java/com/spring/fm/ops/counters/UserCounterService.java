package com.spring.fm.ops.counters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class UserCounterService {

    private final Counter userRegisterCounter;

    public UserCounterService(MeterRegistry meterRegistry) {
        this.userRegisterCounter = meterRegistry.counter("service.user.register");
    }

    public void countUserRegister() {
        this.userRegisterCounter.increment();
    }
}
