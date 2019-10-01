package com.spring.fm.service.implementations;

import com.spring.fm.dto.ValidationErrorMessages;
import com.spring.fm.service.interfaces.IListValidationErrorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Service
public class ListValidationErrorService implements IListValidationErrorService {
    @Override
    public ResponseEntity<?> listValidationService(BindingResult result) {
        if(result.hasErrors()){
            ValidationErrorMessages messages = new ValidationErrorMessages();
            for (FieldError error : result.getFieldErrors()) {
                messages.addErrorMessage(error.getDefaultMessage());
            }
            return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
