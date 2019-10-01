package com.spring.fm.dto;

import com.spring.fm.model.Endurance;
import com.spring.fm.model.Strength;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
public class WorkoutDto {
    private UUID uuid;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Type is required")
    private String type;
    private Strength strength;
    private Endurance endurance;
}
