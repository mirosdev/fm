package com.spring.fm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MealDto {
    private UUID uuid;
    @NotBlank(message = "Name is required")
    private String name;
    private List<String> ingredients;
}
