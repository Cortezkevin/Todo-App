package com.kevin.todo_app.exception;

public class AlreadyExistsResourceWithFieldException extends Exception{
    public AlreadyExistsResourceWithFieldException() {
        super("Resource already exists.");
    }

    public AlreadyExistsResourceWithFieldException(String message) {
        super(message);
    }

    public AlreadyExistsResourceWithFieldException(String resourceName, String field, String value) {
        super("Resource: " + resourceName + ", already exists with " + field + ": " + value + ".");
    }
}
