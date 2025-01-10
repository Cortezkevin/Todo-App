package com.kevin.todo_app.exception.custom;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException() {
        super("Resource not found.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super("Resource: " + resourceName + ", not found with " + field + ": " + value + ".");
    }
}
