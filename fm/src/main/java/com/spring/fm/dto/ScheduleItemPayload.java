package com.spring.fm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ScheduleItemPayload {
    private Set<UUID> meals;
    private Set<UUID> workouts;
    @NotBlank(message = "")
    private String section;
    @NotBlank(message = "")
    private String timestamp;
}
