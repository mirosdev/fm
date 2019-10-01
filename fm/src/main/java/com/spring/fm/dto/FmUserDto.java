package com.spring.fm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FmUserDto {
    private UUID uuid;
    private String email;
}
