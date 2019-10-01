package com.spring.fm.security;

public class InvalidLoginResponse {
    private String message;

    public InvalidLoginResponse() {
        this.message = "Invalid email or password";
    }

    public String getMessage() {
        return message;
    }
}
