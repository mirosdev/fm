package com.spring.fm.security;

import lombok.Data;

@Data
public class GoogleLoginResponse {
    private String issued_to;
    private String email;
}
