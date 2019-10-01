package com.spring.fm.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GoogleLoginRequest {
    @NotBlank(message = "")
    private String token;
}
