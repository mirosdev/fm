package com.spring.fm.ops;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

//@Component
public class HealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        if (check()) {
            return Health.up().build();
        }
        return Health.outOfService().build();
    }

    /**
     * This is the actual health logic
     */
    private boolean check() {
        return false;
    }
}
