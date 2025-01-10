package com.kevin.todo_app.exception.custom;

import lombok.Getter;

import java.util.Map;

@Getter
public class ConstraintsFieldException extends Exception {

    private final Map<String, Object> fieldsErrors;

    public ConstraintsFieldException(Map<String, Object> fieldsErrors, String message) {
        super(message);
        this.fieldsErrors = fieldsErrors;
    }

}
