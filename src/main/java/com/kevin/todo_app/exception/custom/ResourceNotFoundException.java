package com.kevin.todo_app.exception.custom;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends Exception {
    private String resourceName;

    public ResourceNotFoundException() {
        super("Resource not found.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super("Resource: " + resourceName + ", not found with " + field + ": " + value + ".");
        this.resourceName = resourceName;
    }
}
