package com.kevin.todo_app.exception.custom;

import lombok.Getter;

@Getter
public class AlreadyExistsResourceWithFieldException extends Exception{

    private String resourceName;

    public AlreadyExistsResourceWithFieldException() {
        super("Resource already exists.");
    }

    public AlreadyExistsResourceWithFieldException(String message) {
        super(message);
    }

    public AlreadyExistsResourceWithFieldException(String resourceName, String field, String value) {
        super("Resource: " + resourceName + ", already exists with " + field + ": " + value + ".");
        this.resourceName = resourceName;
    }
}
