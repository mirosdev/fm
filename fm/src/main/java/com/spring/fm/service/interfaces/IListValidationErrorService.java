package com.spring.fm.service.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface IListValidationErrorService {
    ResponseEntity<?> listValidationService(BindingResult result);
}
