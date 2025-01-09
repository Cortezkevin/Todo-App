package com.kevin.todo_app.helpers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ObjectValidator {

    private final Validator validator;

    @SneakyThrows
    public <T> T validate(T object){
        Set<ConstraintViolation<T>> errors = validator.validate(object);
        Map<String, Object> fieldErrors = new HashMap<>();
        if( errors.isEmpty() ){
            return object;
        }else {
            errors.forEach(e -> {
                fieldErrors.put(e.getPropertyPath().toString(), e.getMessage());
            });
            throw new RuntimeException("The fields are not valid.");
            //throw new ConstraintsFieldException(fieldErrors, HttpStatus.BAD_REQUEST, "The fields are not valid");
        }
    }
}
