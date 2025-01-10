package com.kevin.todo_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ConstraintsFieldException extends Exception {

    private final Map<String, Object> fieldsErrors;

    public ConstraintsFieldException(Map<String, Object> fieldsErrors, String message) {
        super(message);
        this.fieldsErrors = fieldsErrors;
    }

}
