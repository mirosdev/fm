package com.spring.fm.ops;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpsBeansConfiguration {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> addMealRegistry() {
        return registry -> registry.config().namingConvention().name("service.meal.add", Meter.Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> deleteMealRegistry() {
        return registry -> registry.config().namingConvention().name("service.meal.delete", Meter.Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> addWorkoutRegistry() {
        return registry -> registry.config().namingConvention().name("service.workout.add", Meter.Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> deleteWorkoutRegistry() {
        return registry -> registry.config().namingConvention().name("service.workout.delete", Meter.Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> addScheduleItemRegistry() {
        return registry -> registry.config().namingConvention().name("service.schedule-item.add", Meter.Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> deleteScheduleItemRegistry() {
        return registry -> registry.config().namingConvention().name("service.schedule-item.delete", Meter.Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> registerUserRegistry() {
        return registry -> registry.config().namingConvention().name("service.user.register", Meter.Type.COUNTER);
    }
}
