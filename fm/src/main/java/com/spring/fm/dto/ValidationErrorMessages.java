package com.spring.fm.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorMessages {
    @Getter
    private List<String> messages;

    public ValidationErrorMessages() {
        this.messages = new ArrayList<>();
    }

    public void addErrorMessage(String message) {
        this.messages.add(message);
    }
}
