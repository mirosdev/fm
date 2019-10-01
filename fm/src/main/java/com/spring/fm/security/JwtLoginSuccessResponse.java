package com.spring.fm.security;

import lombok.Data;

@Data
public class JwtLoginSuccessResponse {
    private String token;

    public JwtLoginSuccessResponse(String token) {
        this.token = token;
    }
}
